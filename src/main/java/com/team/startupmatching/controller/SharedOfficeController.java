package com.team.startupmatching.controller;

import com.team.startupmatching.dto.*;
import com.team.startupmatching.service.SharedOfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shared-offices")
public class SharedOfficeController {

    private final SharedOfficeService sharedOfficeService;

    // 등록
    @PostMapping
    public ResponseEntity<SharedOfficeResponse> create(@Valid @RequestBody SharedOfficeCreateRequest request) {
        SharedOfficeResponse created = sharedOfficeService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")                         // Location: /api/shared-offices/{id}
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created); // 201 Created
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<SharedOfficeDetailResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(sharedOfficeService.getOne(id));
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<List<SharedOfficeResponse>> list() {
        return ResponseEntity.ok(sharedOfficeService.list());
    }

    // 공유오피스 추천
    @PostMapping("/recommend")
    public ResponseEntity<List<SharedOfficeRecommendResponse>> recommend(
            @Valid @RequestBody SharedOfficeRecommendRequest request
    ) {
        List<SharedOfficeRecommendResponse> recommendations =
                sharedOfficeService.recommendByLocation(request.getLocation());
        return ResponseEntity.ok(recommendations);
    }
}
