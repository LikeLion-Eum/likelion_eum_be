package com.team.startupmatching.controller;

import com.team.startupmatching.dto.RecruitmentRequest;
import com.team.startupmatching.dto.RecruitmentResponse;
import com.team.startupmatching.dto.RecruitmentSearchRequest;
import com.team.startupmatching.dto.common.SpaceType;
import com.team.startupmatching.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 모집글 검색(또는 전체 목록 조회) API
     * GET /api/recruitment/list?keyword=신창
     */
    @GetMapping("/list")
    public ResponseEntity<List<RecruitmentResponse>> listAll() {
        return ResponseEntity.ok(recruitmentService.listAll());
    }
    @PostMapping("/search")
    public ResponseEntity<List<RecruitmentResponse>> search(@RequestBody RecruitmentSearchRequest req) {
        return ResponseEntity.ok(
                recruitmentService.search(req.getKeyword(), req.getTargetSpaceType(), req.getKeywords())
        );
    }

}