package com.team.startupmatching.controller;

import com.team.startupmatching.dto.LoveCallCreateRequest;
import com.team.startupmatching.dto.LoveCallResponse;
import com.team.startupmatching.service.LoveCallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoveCallController {

    private final LoveCallService loveCallService;

    /** v2 권장: DTO 본문으로 생성 (senderId는 인증 주체로 강제 세팅) */
    @PostMapping("/love-calls")
    public ResponseEntity<LoveCallResponse> create(
            @Valid @RequestBody LoveCallCreateRequest req
    ) {
        // 해커톤용: sender는 바디의 senderId가 있으면 사용, 없으면 1L
        Long sender = (req.senderId() != null) ? req.senderId() : 1L;

        LoveCallCreateRequest fixed = new LoveCallCreateRequest(
                req.recruitmentId(), req.recipientId(), sender, req.message()
        );
        LoveCallResponse res = loveCallService.send(fixed);
        return ResponseEntity.created(URI.create("/api/love-calls/" + res.id())).body(res);
    }

    /** v1 호환: /recruitments/{id}/love-calls (기존 프론트와 호환용) */
    @PostMapping("/recruitments/{recruitmentId}/love-calls")
    public ResponseEntity<LoveCallResponse> createLegacy(
            @PathVariable Long recruitmentId,
            @RequestBody LegacySendLoveCallRequest req,
            @AuthenticationPrincipal(expression = "id") Long me
    ) {
        LoveCallCreateRequest dto = new LoveCallCreateRequest(
                recruitmentId,
                req.recipientUserId(),
                me,
                req.message()
        );
        LoveCallResponse res = loveCallService.send(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/me/love-calls/received")
    public ResponseEntity<Page<LoveCallResponse>> listReceived(
            @RequestParam(name = "userId", required = false) Long userId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long uid = (userId != null) ? userId : 1L;
        return ResponseEntity.ok(loveCallService.listReceived(uid, pageable));
    }

    /** 보낸 러브콜 목록 */
    @GetMapping("/me/love-calls/sent")
    public ResponseEntity<Page<LoveCallResponse>> listSent(
            @RequestParam(name = "userId", required = false) Long userId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long uid = (userId != null) ? userId : 1L;
        return ResponseEntity.ok(loveCallService.listSent(uid, pageable));
    }

    /** 상세 + 읽음 처리 옵션 ?markRead=true (수신자 권한) */
    @GetMapping("/me/love-calls/{id}")
    public ResponseEntity<LoveCallResponse> getDetail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean markRead,
            @AuthenticationPrincipal(expression = "id") Long me
    ) {
        LoveCallResponse lc = loveCallService.getDetailForRecipient(id, me, markRead);
        return ResponseEntity.ok(lc);
    }

    /** 명시적 읽음 처리 (204) */
    @PostMapping("/me/love-calls/{id}/read")
    public ResponseEntity<Void> markRead(
            @PathVariable Long id,
            @RequestParam(name = "userId", required = false) Long userId
    ) {
        Long uid = (userId != null) ? userId : 1L;
        loveCallService.markRead(id, uid);
        return ResponseEntity.ok().build();
    }

    /** 미읽음 개수(헤더 뱃지) */
    @GetMapping("/me/love-calls/unread-count")
    public ResponseEntity<Long> unreadCount(
            @AuthenticationPrincipal(expression = "id") Long me
    ) {
        return ResponseEntity.ok(loveCallService.unreadCount(me));
    }


    @DeleteMapping("/me/love-calls/{id}")
    public ResponseEntity<Void> deleteOne(
            @PathVariable Long id,
            @RequestParam("userId") Long userId   // ★ Principal 대신 명시적으로 받기
    ) {
        loveCallService.deleteAsSender(id, userId);
        return ResponseEntity.noContent().build(); // 204
    }

    /** (구버전 호환) 요청 Body */
    public record LegacySendLoveCallRequest(Long recipientUserId, String message) {}
}
