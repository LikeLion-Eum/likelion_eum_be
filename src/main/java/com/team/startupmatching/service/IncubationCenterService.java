package com.team.startupmatching.service;

import com.team.startupmatching.dto.IncubationCenterCreateRequest;
import com.team.startupmatching.dto.IncubationCenterResponse;
import com.team.startupmatching.entity.IncubationCenter;
import com.team.startupmatching.event.IncubationCenterChangedEvent;
import com.team.startupmatching.repository.IncubationCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IncubationCenterService {

    private final IncubationCenterRepository incubationCenterRepository;
    private final ApplicationEventPublisher publisher;

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

        // ✅ 저장 성공 후 업서트 트리거 이벤트 발행 (리스너가 AFTER_COMMIT에서 처리)
        publisher.publishEvent(new IncubationCenterChangedEvent(saved.getId()));

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

    // 검색 (region/recruiting/openOn)
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

    @Transactional(readOnly = true)
    public IncubationCenterResponse getOne(Long id) {
        var e = incubationCenterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "incubation-center not found: " + id));

        // 엔티티 -> 응답 DTO 매핑 (필드명은 프로젝트에 맞게 조정)
        return IncubationCenterResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .region(e.getRegion())
                .supportField(e.getSupportField())
                .applyUrl(e.getApplyUrl())
                .receiptStartDate(e.getReceiptStartDate())
                .receiptEndDate(e.getReceiptEndDate())
                .recruiting(e.isRecruiting())
                .build();
    }

}
