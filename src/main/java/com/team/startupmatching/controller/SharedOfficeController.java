package com.team.startupmatching.controller;

import com.team.startupmatching.dto.*;
import com.team.startupmatching.service.SharedOfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shared-offices")
public class SharedOfficeController {

    private final SharedOfficeService sharedOfficeService;

    @PostMapping
    public ResponseEntity<SharedOfficeResponse> create(@Valid @RequestBody SharedOfficeCreateRequest request) {
        SharedOfficeResponse created = sharedOfficeService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SharedOfficeDetailResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(sharedOfficeService.getOne(id));
    }

    @GetMapping
    public ResponseEntity<Page<SharedOfficeResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String minFeeMonthly,
            @RequestParam(required = false) String maxFeeMonthly,
            @RequestParam(required = false, name = "minPrice") String minPrice,
            @RequestParam(required = false, name = "maxPrice") String maxPrice,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim()
                : (q != null ? q.trim() : null);

        Long min = firstNonNullLong(toLong(minFeeMonthly), toLong(minPrice));
        Long max = firstNonNullLong(toLong(maxFeeMonthly), toLong(maxPrice));

        int safePage = Math.max(1, page);
        int safeSize = Math.min(Math.max(1, size), 100);

        Page<SharedOfficeResponse> result =
                sharedOfficeService.search(kw, min, max, safePage, safeSize);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/recommend")
    public ResponseEntity<List<SharedOfficeRecommendResponse>> recommend(
            @Valid @RequestBody SharedOfficeRecommendRequest request
    ) {
        return ResponseEntity.ok(
                sharedOfficeService.recommendByLocation(request.getLocation())
        );
    }

    /* ====== 추가: 수정(전체/부분) & 삭제 ====== */

    /** 전체수정(치환) — 존재하는 모든 필드 필수 */
    @PutMapping("/{id}")
    public ResponseEntity<SharedOfficeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SharedOfficeUpdateRequest request
    ) {
        return ResponseEntity.ok(sharedOfficeService.update(id, request));
    }

    /** 부분수정(패치) — null 이 아닌 필드만 반영 */
    @PatchMapping("/{id}")
    public ResponseEntity<SharedOfficeResponse> patch(
            @PathVariable Long id,
            @RequestBody SharedOfficePatchRequest request
    ) {
        return ResponseEntity.ok(sharedOfficeService.patch(id, request));
    }

    /** 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sharedOfficeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ===== helpers ===== */

    private static final Pattern NON_DIGIT = Pattern.compile("\\D");

    private Long toLong(String v) {
        if (v == null) return null;
        String digits = NON_DIGIT.matcher(v).replaceAll("");
        if (digits.isEmpty()) return null;
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private Long firstNonNullLong(Long a, Long b) {
        return (a != null) ? a : b;
    }
}
