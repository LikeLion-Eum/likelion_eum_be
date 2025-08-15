package com.team.startupmatching.controller;


import com.team.startupmatching.dto.SharedOfficeCreateRequest;
import com.team.startupmatching.dto.SharedOfficeRecommendRequest;
import com.team.startupmatching.dto.SharedOfficeRecommendResponse;
import com.team.startupmatching.dto.SharedOfficeResponse;
import com.team.startupmatching.service.SharedOfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shared-offices")
public class SharedOfficeController {

    private final SharedOfficeService sharedOfficeService;

    @PostMapping
    public SharedOfficeResponse create(@RequestBody SharedOfficeCreateRequest request) {
        return sharedOfficeService.create(request);
    }

    @GetMapping
    public List<SharedOfficeResponse> list() {
        return sharedOfficeService.list();
    }

    // 공유 오피스 추천
    @PostMapping("/recommend")
    public ResponseEntity<List<SharedOfficeRecommendResponse>> recommend(
            @Valid @RequestBody SharedOfficeRecommendRequest request
    ) {
        List<SharedOfficeRecommendResponse> recommendations = sharedOfficeService.recommendByLocation(request.getLocation());
        return ResponseEntity.ok(recommendations);
    }
}