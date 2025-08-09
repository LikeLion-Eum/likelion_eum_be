// src/main/java/com/team/startupmatching/external/kstartup/KStartupClient.java
package com.team.startupmatching.external.kstartup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class KStartupClient {

    @Value("${kstartup.base-url}")
    private String baseUrl;

    @Value("${kstartup.service-key}")
    private String serviceKey;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();
    private final XmlMapper xml = new XmlMapper();

    private static final DateTimeFormatter YMD = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    /** 한 페이지 조회: JSON이면 JSON으로, XML이면 XML 파싱해서 items 반환 */
    public List<JsonNode> fetchPage(String regionLike, int page, int perPage,
                                    boolean onlyOpen, LocalDate endDateGte) {

        String url = buildUrl(regionLike, page, perPage, onlyOpen, endDateGte);
        String body = rest.getForObject(url, String.class);
        if (body == null || body.isBlank()) return List.of();

        String t = body.trim();

        // 1) JSON 응답
        if (t.startsWith("{") || t.startsWith("[")) {
            try {
                JsonNode root = om.readTree(t);
                return extractItemsFromJson(root);
            } catch (Exception e) {
                log.error("K-Startup JSON 파싱 실패. 응답 일부: {}", preview(t));
                throw new IllegalStateException("K-Startup API JSON 파싱 실패", e);
            }
        }

        // 2) XML 응답 (정상 데이터 또는 에러)
        try {
            if (t.contains("<OpenAPI_ServiceResponse")) { // 공통 에러 포맷
                String err = between(t, "<errMsg>", "</errMsg>");
                String auth = between(t, "<returnAuthMsg>", "</returnAuthMsg>");
                String code = between(t, "<returnReasonCode>", "</returnReasonCode>");
                String msg = "K-Startup API 오류: "
                        + (err != null ? err : "UNKNOWN")
                        + (auth != null ? " / " + auth : "")
                        + (code != null ? " / code=" + code : "");
                throw new IllegalStateException(msg);
            }
            // 정상 데이터(<results> ...)
            JsonNode root = xml.readTree(t);
            return extractItemsFromXml(root);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("K-Startup XML 파싱 실패. 응답 일부: {}", preview(t));
            throw new IllegalStateException("K-Startup API XML 파싱 실패", e);
        }
    }

    private String buildUrl(String regionLike, int page, int perPage,
                            boolean onlyOpen, LocalDate endDateGte) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("serviceKey", serviceKey)   // Decoding 키 (자동 인코딩됨)
                .queryParam("_type", "json")            // JSON 요청 시도(일부는 무시하고 XML로 줌)
                .queryParam("page", page)
                .queryParam("perPage", perPage);

        if (regionLike != null && !regionLike.isBlank()) {
            b.queryParam("cond[supt_regin::LIKE]", regionLike);
        }
        if (onlyOpen) {
            b.queryParam("cond[rcrt_prgs_yn::EQ]", "Y");
        }
        if (endDateGte != null) {
            b.queryParam("cond[pbanc_rcpt_end_dt::GTE]", endDateGte.format(YMD));
        }
        return b.build().toUriString();
    }

    /* ---------- JSON 응답 파서 ---------- */
    private List<JsonNode> extractItemsFromJson(JsonNode root) {
        List<JsonNode> list = new ArrayList<>();
        if (root == null) return list;

        JsonNode data = root.path("data");
        if (data.isMissingNode()) data = root.path("results").path("data");
        if (data.isObject()) data = data.path("data");
        if (data.isArray()) data.forEach(list::add);

        return list;
    }

    /* ---------- XML 응답 파서 ----------
       <results>
         <currentCount>...</currentCount>
         <data>
           <item>
             <col name="biz_pbanc_nm">...</col>
             <col name="supt_regin">충남</col>
             ...
           </item>
         </data>
       </results>
     */
    private List<JsonNode> extractItemsFromXml(JsonNode xmlRoot) {
        List<JsonNode> list = new ArrayList<>();
        if (xmlRoot == null) return list;

        // root가 <results> 이거나 Object{results:{...}} 인 두 경우 모두 대응
        JsonNode base = xmlRoot.has("results") ? xmlRoot.get("results") : xmlRoot;
        JsonNode data = base.path("data");
        JsonNode items = data.path("item");
        if (items.isMissingNode() || items.isNull()) return list;

        if (items.isArray()) {
            for (JsonNode item : items) list.add(flattenXmlItem(item));
        } else {
            list.add(flattenXmlItem(items));
        }
        return list;
    }

    /** <item><col name="KEY">VALUE</col>...</item> 를 { "KEY": "VALUE", ... } 로 평탄화 */
    private JsonNode flattenXmlItem(JsonNode item) {
        ObjectNode obj = om.createObjectNode();
        JsonNode cols = item.get("col");
        if (cols == null) return obj;

        if (cols.isArray()) {
            for (JsonNode c : cols) putCol(obj, c);
        } else {
            putCol(obj, cols);
        }
        return obj;
    }
    private String nodeText(JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isValueNode()) return node.asText();

        // Jackson XML이 본문 텍스트를 "" 또는 "#text" 로 넣는 경우가 있음
        JsonNode t = node.get("");
        if (t != null && !t.isNull()) return t.asText();
        t = node.get("#text");
        if (t != null && !t.isNull()) return t.asText();

        // 그 외: 속성(@...)이 아닌 첫 필드의 텍스트를 사용
        var fields = node.fieldNames();
        while (fields.hasNext()) {
            String k = fields.next();
            if (!k.startsWith("@")) {
                JsonNode v = node.get(k);
                if (v != null && !v.isNull()) return v.asText();
            }
        }
        return null;
    }

    private void putCol(ObjectNode obj, JsonNode col) {
        String key = attr(col, "@name");
        if (key == null) key = attr(col, "name"); // 드물게 @ 없이 매핑될 수 있음
        String val = nodeText(col);             // 태그 텍스트값
        if (key != null) obj.put(key, val);
    }

    private String attr(JsonNode node, String name) {
        JsonNode a = node.get(name);
        return a == null || a.isNull() ? null : a.asText();
    }

    private String between(String s, String a, String b) {
        int i = s.indexOf(a); if (i < 0) return null;
        int j = s.indexOf(b, i + a.length()); if (j < 0) return null;
        return s.substring(i + a.length(), j);
    }

    private String preview(String s) {
        return s.substring(0, Math.min(200, s.length()));
    }
}
