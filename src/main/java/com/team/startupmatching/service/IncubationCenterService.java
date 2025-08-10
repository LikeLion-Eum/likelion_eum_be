package com.team.startupmatching.service;

import com.team.startupmatching.dto.IncubationCenterCreateRequest;
import com.team.startupmatching.dto.IncubationCenterResponse;
import com.team.startupmatching.entity.IncubationCenter;
import com.team.startupmatching.repository.IncubationCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IncubationCenterService {

    private final IncubationCenterRepository incubationCenterRepository;

    // 업서트(있으면 업데이트, 없으면 신규)
    public IncubationCenterResponse create(IncubationCenterCreateRequest request) {
        String sourceId = requireNonBlank(request.getSourceId(), "sourceId");

        IncubationCenter entity = incubationCenterRepository.findBySourceId(sourceId)
                .orElseGet(() -> IncubationCenter.builder().build());


        if (entity.getId() == null) {
            entity.setSourceId(sourceId);
        }

        entity.setTitle(requireNonBlank(request.getTitle(), "title"));
        entity.setRegion(requireNonBlank(request.getRegion(), "region"));
        entity.setSupportField(nullToEmpty(request.getSupportField()));
        entity.setReceiptStartDate(request.getReceiptStartDate());
        entity.setReceiptEndDate(request.getReceiptEndDate());
        entity.setRecruiting(Boolean.TRUE.equals(request.getRecruiting()));
        entity.setApplyUrl(nullToEmpty(request.getApplyUrl()));

        IncubationCenter saved = incubationCenterRepository.save(entity);
        return IncubationCenterResponse.from(saved);
    }

    // 전체 목록
    @Transactional(readOnly = true)
    public List<IncubationCenterResponse> list() {
        return incubationCenterRepository.findAll().stream()
                .map(IncubationCenterResponse::from)
                .collect(Collectors.toList());
    }

    // ---- helpers ----
    private String requireNonBlank(String v, String field) {
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return v;
    }
    private String nullToEmpty(String v) {
        return Objects.toString(v, "");
    }

    // IncubationCenterService.java 내부
    @Transactional(readOnly = true)
    public List<IncubationCenterResponse> search(String region, Boolean recruiting, LocalDate openOn) {
        List<IncubationCenter> base;

        // region 우선 필터
        if (region != null && !region.isBlank()) {
            base = incubationCenterRepository.findByRegionContainingIgnoreCase(region);
        } else {
            base = incubationCenterRepository.findAll();
        }

        // recruiting 필터
        if (recruiting != null) {
            base = base.stream()
                    .filter(e -> e.isRecruiting() == recruiting)
                    .toList();
        }

        // openOn: start <= openOn <= end
        if (openOn != null) {
            base = base.stream()
                    .filter(e ->
                            (e.getReceiptStartDate() == null || !e.getReceiptStartDate().isAfter(openOn)) &&
                                    (e.getReceiptEndDate() == null   || !e.getReceiptEndDate().isBefore(openOn))
                    )
                    .toList();
        }

        return base.stream().map(IncubationCenterResponse::from).toList();
    }

    public org.springframework.data.domain.Page<IncubationCenterResponse> searchKeyword(
            String keyword, Boolean recruiting, org.springframework.data.domain.Pageable pageable) {

        String kw = (keyword == null) ? "" : keyword.trim();
        var page = incubationCenterRepository.searchByKeyword(kw, recruiting, pageable);

        return page.map(ic -> new IncubationCenterResponse(
                ic.getId(),
                ic.getSourceId(),
                ic.getTitle(),
                ic.getRegion(),
                ic.getSupportField(),
                ic.getReceiptStartDate(),
                ic.getReceiptEndDate(),
                ic.isRecruiting(),
                ic.getApplyUrl()
        ));
    }

}
