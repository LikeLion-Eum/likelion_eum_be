package com.team.startupmatching.controller;

import com.team.startupmatching.dto.RecruitmentRequest;
import com.team.startupmatching.dto.RecruitmentResponse;
import com.team.startupmatching.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    /**
     * 모집글 등록 API
     * POST /api/recruitment
     */
    @PostMapping
    public ResponseEntity<RecruitmentResponse> createRecruitment(@RequestBody RecruitmentRequest request) {
        RecruitmentResponse response = recruitmentService.createRecruitment(request);
        return ResponseEntity.ok(response);
    }
}