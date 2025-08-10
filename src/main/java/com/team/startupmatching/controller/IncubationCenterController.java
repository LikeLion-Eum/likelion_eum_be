package com.team.startupmatching.controller;

import com.team.startupmatching.dto.IncubationCenterCreateRequest;
import com.team.startupmatching.dto.IncubationCenterResponse;
import com.team.startupmatching.service.IncubationCenterService;
import com.team.startupmatching.service.KStartupImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/incubation-centers")
public class IncubationCenterController {

    private final IncubationCenterService incubationCenterService;
    private final KStartupImportService kStartupImportService;


    /** 업서트(있으면 업데이트, 없으면 신규) */
    @PostMapping
    public IncubationCenterResponse create(@RequestBody IncubationCenterCreateRequest request) {
        return incubationCenterService.create(request);
    }

    /** 전체 목록 */
    @GetMapping
    public List<IncubationCenterResponse> list() {
        return incubationCenterService.list();
    }

    /**
     * 검색 - 파라미터 모두 선택사항
     * region: 지역(부분일치)
     * recruiting: 모집중만(true/false)
     * openOn: 해당 날짜에 접수중인지( start <= openOn <= end )
     */
    // IncubationCenterController.java

    @GetMapping("/search")
    public org.springframework.data.domain.Page<IncubationCenterResponse> search(
            @RequestParam("keyword") String keyword,
            @RequestParam(required = false) Boolean recruiting,
            @org.springframework.data.web.PageableDefault(
                    size = 20,
                    sort = "receiptEndDate",
                    direction = org.springframework.data.domain.Sort.Direction.ASC
            ) org.springframework.data.domain.Pageable pageable
    ) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("keyword는 필수입니다.");
            // 또는 return Page.empty(pageable);
        }
        return incubationCenterService.searchKeyword(keyword, recruiting, pageable);
    }



    // 엔드포인트 추가
    @PostMapping("/sync")
    public String sync(
            @RequestParam(required = false) String region, // 예: 충남 / 아산
            @RequestParam(defaultValue = "true") boolean onlyOpen, // 모집중만
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDateGte // 마감일 >= 이 날짜
    ) {
        int n = kStartupImportService.sync(region, onlyOpen, endDateGte);
        return "synced: " + n;
    }


    @PostMapping("/sync-batch")
    public String syncBatch(
            @RequestParam(required = false) String regions,   // "서울,경기,인천" 또는 미입력(전국)
            @RequestParam(defaultValue = "true") boolean onlyOpen,
            @RequestParam(defaultValue = "120") int daysAhead,
            @RequestParam(defaultValue = "true") boolean requireDates,
            @RequestParam(defaultValue = "true") boolean integratedOnly,
            @RequestParam(defaultValue = "3000") int max
    ) {
        List<String> regionList = (regions == null || regions.isBlank())
                ? List.of()
                : Arrays.stream(regions.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        int n = kStartupImportService.syncLimited(regionList, onlyOpen, daysAhead, requireDates, integratedOnly, max);
        return "synced: " + n;
    }
}
