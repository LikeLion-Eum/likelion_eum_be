package com.team.startupmatching.controller;

import com.team.startupmatching.dto.RecruitmentRequest;
import com.team.startupmatching.dto.RecruitmentResponse;
import com.team.startupmatching.dto.RecruitmentSearchRequest;
import com.team.startupmatching.dto.RecruitmentUpdateRequest;
import com.team.startupmatching.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    /** 모집글 등록 */
    @PostMapping
    public ResponseEntity<RecruitmentResponse> createRecruitment(@RequestBody RecruitmentRequest request) {
        RecruitmentResponse response = recruitmentService.createRecruitment(request);
        // 보통 201 Created + Location 헤더, 지금 구조 유지하고 싶으면 OK로 둬도 무방
        return ResponseEntity.ok(response);
        // return ResponseEntity.created(URI.create("/api/recruitments/" + response.getId())).body(response);
    }

    /** 전체 목록 조회 (최신순) */
    @GetMapping({"", "/", "/list"})
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

    /** 단건 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<RecruitmentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(recruitmentService.getOne(id));
    }

    /* --------------------------
     * 수정/삭제 (공유오피스와 동일 패턴)
     * -------------------------- */

    /** 전체 치환(모든 필드 필수) */
    @PutMapping("/{id}")
    public ResponseEntity<RecruitmentResponse> updateReplace(
            @PathVariable Long id,
            @RequestBody RecruitmentRequest request
    ) {
        return ResponseEntity.ok(recruitmentService.updateReplace(id, request));
    }

    /** 부분 수정(넘어온 필드만) */
    @PatchMapping("/{id}")
    public ResponseEntity<RecruitmentResponse> updatePartial(
            @PathVariable Long id,
            @RequestBody RecruitmentUpdateRequest request
    ) {
        return ResponseEntity.ok(recruitmentService.updatePartial(id, request));
    }

    /** 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recruitmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
