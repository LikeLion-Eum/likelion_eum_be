package com.team.startupmatching.controller;

import com.team.startupmatching.dto.RecruitmentResponse;
import com.team.startupmatching.dto.IncubationCenterCreateRequest;
import com.team.startupmatching.dto.RecruitmentSearchRequest;
import com.team.startupmatching.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.team.startupmatching.dto.RecruitmentRequest;
import java.util.List;

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    /** 모집글 등록 */
    @PostMapping
    public ResponseEntity<RecruitmentResponse> createRecruitment(@RequestBody RecruitmentRequest request) {
        RecruitmentResponse response = recruitmentService.createRecruitment(request);
        return ResponseEntity.ok(response);
    }

    /** 전체 목록 조회 (최신순) */
    @GetMapping("/list")
    public ResponseEntity<List<RecruitmentResponse>> listAll() {
        return ResponseEntity.ok(recruitmentService.listAll());
    }

    /** 검색 (키워드 배열 AND 또는 단일 키워드 공백 AND) */
    @PostMapping("/search")
    public ResponseEntity<List<RecruitmentResponse>> search(@RequestBody RecruitmentSearchRequest req) {
        return ResponseEntity.ok(
                recruitmentService.search(req.getKeyword(), req.getKeywords())
        );
    }
}
