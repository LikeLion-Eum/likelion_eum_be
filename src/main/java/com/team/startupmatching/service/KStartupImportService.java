// src/main/java/com/team/startupmatching/service/KStartupImportService.java
package com.team.startupmatching.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.team.startupmatching.entity.IncubationCenter;
import com.team.startupmatching.external.kstartup.KStartupClient;
import com.team.startupmatching.repository.IncubationCenterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KStartupImportService {

    private final KStartupClient client;
    private final IncubationCenterRepository repo;

    private static final int PER_PAGE = 100;
    private static final DateTimeFormatter YMD = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    /**
     * regionLike 예: "충남"/"아산"(null이면 전체)
     * onlyOpen   모집중만(Y) 필터
     * endDateGte 마감일 >= 이 날짜 (null이면 오늘)
     */
    @Transactional
    public int sync(String regionLike, Boolean onlyOpen, LocalDate endDateGte) {
        int saved = 0;
        LocalDate threshold = (endDateGte != null) ? endDateGte : LocalDate.now();

        int page = 1;
        while (true) {
            List<JsonNode> items = client.fetchPage(regionLike, page, PER_PAGE,
                    onlyOpen != null && onlyOpen, threshold);
            if (items.isEmpty()) break;

            for (JsonNode it : items) {
                if (upsert(it)) saved++;           // 저장/업데이트 성공 시에만 카운트
            }
            if (items.size() < PER_PAGE) break;   // 마지막 페이지
            page++;
        }
        return saved;
    }

    /** 한 건 업서트: 성공 시 true */
    private boolean upsert(JsonNode it) {
        // 1) sourceId 생성 (pbanc_sn → id → 해시 fallback)
        String sourceId = buildSourceId(it);
        if (isBlank(sourceId)) {
            log.warn("skip: no sourceId. title={}", get(it, "biz_pbanc_nm"));
            return false;
        }

        // 2) 매핑
        String title   = nvl(get(it, "biz_pbanc_nm"));
        String region  = nvl(get(it, "supt_regin"));
        String field   = nvl(get(it, "supt_biz_clsfc"));

        LocalDate start = parseYmd(get(it, "pbanc_rcpt_bgng_dt"));
        LocalDate end   = parseYmd(get(it, "pbanc_rcpt_end_dt"));

        boolean recruiting = "Y".equalsIgnoreCase(get(it, "rcrt_prgs_yn"));

        // ✅ 진짜 URL만 고르기 (값 검증/정리 포함)
        String applyUrl = pickUrl(it,
                "detl_pg_url",                 // 상세 페이지
                "aply_mthd_onli_rcpt_istc",    // 온라인 접수
                "biz_gdnc_url",                // 안내문/가이드
                "biz_aply_url"                 // (비어있는 경우가 많음)
        );

        // (옵션) 최소 요건 체크: 제목/URL/날짜 모두 없으면 스킵
        if (isBlank(title) && isBlank(applyUrl) && start == null && end == null) {
            log.warn("skip: essential fields missing (no title/url/dates)");
            return false;
        }

        // 3) 업서트
        IncubationCenter e = repo.findBySourceId(sourceId)
                .orElseGet(() -> IncubationCenter.builder().sourceId(sourceId).build());

        e.setTitle(title);
        e.setRegion(region);
        e.setSupportField(field);
        e.setReceiptStartDate(start);
        e.setReceiptEndDate(end);
        e.setRecruiting(recruiting);
        e.setApplyUrl(nvl(applyUrl));

        repo.save(e);
        return true;
    }

    /* ----------------- helpers ----------------- */

    /** pbanc_sn → id → (제목|지역|시작|종료|URL) 해시 */
    private String buildSourceId(JsonNode it) {
        String s = firstNonBlank(get(it, "pbanc_sn"), get(it, "id"));
        if (!isBlank(s)) return s.trim();

        String raw = String.join("|",
                nvl(get(it, "biz_pbanc_nm")),
                nvl(get(it, "supt_regin")),
                nvl(get(it, "pbanc_rcpt_bgng_dt")),
                nvl(get(it, "pbanc_rcpt_end_dt")),
                nvl(firstNonBlank(get(it, "detl_pg_url"),
                        get(it, "aply_mthd_onli_rcpt_istc"),
                        get(it, "biz_gdnc_url"),
                        get(it, "biz_aply_url")))
        );
        return sha256(raw);
    }

    /** 여러 후보 중 “유효한 URL”만 선택 */
    private String pickUrl(JsonNode it, String... keys) {
        for (String k : keys) {
            String v = cleanUrl(get(it, k));
            // 값이 비었거나, 키 이름 그대로 들어온 값(biz_aply_url) 차단
            if (isBlank(v) || k.equalsIgnoreCase(v)) continue;
            if (looksLikeUrl(v)) return v;
        }
        return null;
    }

    private String cleanUrl(String s) {
        if (s == null) return null;
        String v = s.trim().replace("&amp;", "&");
        if (v.startsWith("www.")) v = "https://" + v;
        return v;
    }

    private boolean looksLikeUrl(String v) {
        return v.startsWith("http://") || v.startsWith("https://");
    }

    private String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);  // Java 17+
        } catch (Exception e) {
            log.error("SHA-256 생성 실패", e);
            return null;
        }
    }

    private String get(JsonNode n, String key) {
        JsonNode v = n == null ? null : n.get(key);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    private String firstNonBlank(String... arr) {
        for (String s : arr) if (!isBlank(s)) return s;
        return null;
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }

    private String nvl(String s) { return s == null ? "" : s.trim(); }

    private LocalDate parseYmd(String s) {
        if (isBlank(s)) return null;
        return LocalDate.parse(s.trim(), YMD); // yyyyMMdd
    }

    @Transactional
    public int syncLimited(
            java.util.List<String> regions,   // null/빈 → 전국
            boolean onlyOpen,
            int daysAhead,                    // 오늘+N일 이내 마감만
            boolean requireDates,             // 시작/종료일 둘 다 있어야
            boolean integratedOnly,           // intg_pbanc_yn == Y 만
            int maxSaved                      // 이번 싱크 최대 저장 수
    ) {
        int saved = 0;
        java.time.LocalDate endLimit = java.time.LocalDate.now().plusDays(daysAhead);
        java.util.List<String> targets = (regions == null || regions.isEmpty())
                ? java.util.List.of((String) null) : regions; // null이면 전국

        for (String regionLike : targets) {
            int page = 1;
            while (true) {
                var items = client.fetchPage(regionLike, page, PER_PAGE, onlyOpen, java.time.LocalDate.now());
                if (items.isEmpty()) break;

                for (com.fasterxml.jackson.databind.JsonNode it : items) {
                    if (integratedOnly && !"Y".equalsIgnoreCase(get(it, "intg_pbanc_yn"))) continue;

                    java.time.LocalDate s = parseYmd(get(it, "pbanc_rcpt_bgng_dt"));
                    java.time.LocalDate e = parseYmd(get(it, "pbanc_rcpt_end_dt"));

                    if (requireDates && (s == null || e == null)) continue;
                    if (e != null && e.isAfter(endLimit)) continue;

                    if (upsert(it)) {
                        saved++;
                        if (saved >= maxSaved) return saved;
                    }
                }
                if (items.size() < PER_PAGE) break;
                page++;
            }
        }
        return saved;
    }

}
