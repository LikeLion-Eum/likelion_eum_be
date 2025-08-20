package com.team.startupmatching.controller;

import com.team.startupmatching.entity.LoveCall;
import com.team.startupmatching.service.LoveCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoveCallController {

    private final LoveCallService loveCallService;

    /** 러브콜 보내기 (인증 필수: SecurityConfig에서 보호) */
    @PostMapping("/recruitments/{recruitmentId}/love-calls")
    public ResponseEntity<LoveCall> send(
            @PathVariable Long recruitmentId,
            @RequestBody SendLoveCallRequest req,
            @AuthenticationPrincipal(expression = "id") Long me
    ) {
        LoveCall created = loveCallService.send(recruitmentId, me, req.recipientUserId(), req.message());
        return ResponseEntity.ok(created);
    }

    /** 받은 러브콜 목록 (마이페이지 수신함) */
    @GetMapping("/me/love-calls/received")
    public ResponseEntity<Page<LoveCall>> listReceived(
            @AuthenticationPrincipal(expression = "id") Long me,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<LoveCall> page = loveCallService.listReceived(me, pageable);
        return ResponseEntity.ok(page);
    }

    /** 보낸 러브콜 목록 (선택) */
    @GetMapping("/me/love-calls/sent")
    public ResponseEntity<Page<LoveCall>> listSent(
            @AuthenticationPrincipal(expression = "id") Long me,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<LoveCall> page = loveCallService.listSent(me, pageable);
        return ResponseEntity.ok(page);
    }

    /** 상세 + 읽음 처리 옵션 ?markRead=true */
    @GetMapping("/me/love-calls/{id}")
    public ResponseEntity<LoveCall> getDetail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean markRead,
            @AuthenticationPrincipal(expression = "id") Long me
    ) {
        LoveCall lc = loveCallService.getDetailForRecipient(id, me, markRead);
        return ResponseEntity.ok(lc);
    }

    /** 명시적 읽음 처리 */
    @PostMapping("/me/love-calls/{id}/read")
    public ResponseEntity<Void> markRead(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "id") Long me
    ) {
        loveCallService.markRead(id, me);
        return ResponseEntity.ok().build();
    }

    /** 미읽음 개수(헤더 뱃지) */
    @GetMapping("/me/love-calls/unread-count")
    public ResponseEntity<Long> unreadCount(
            @AuthenticationPrincipal(expression = "id") Long me
    ) {
        return ResponseEntity.ok(loveCallService.unreadCount(me));
    }

    /* ===== 요청 DTO (간단히 record로) ===== */
    public record SendLoveCallRequest(Long recipientUserId, String message) {}
}
