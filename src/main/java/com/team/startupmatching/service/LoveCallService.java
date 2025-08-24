package com.team.startupmatching.service;

import com.team.startupmatching.entity.LoveCall;
import com.team.startupmatching.repository.LoveCallRepository;

// ✅ DTO (경로는 네 프로젝트에 맞게 조정)
import com.team.startupmatching.dto.LoveCallCreateRequest;
import com.team.startupmatching.dto.LoveCallResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoveCallService {

    private final LoveCallRepository loveCallRepository;

    /** 러브콜 보내기 (중복 방지) */
    @Transactional
    public LoveCallResponse send(LoveCallCreateRequest req) {
        if (req == null || req.recruitmentId() == null || req.senderId() == null || req.recipientId() == null) {
            throw new IllegalArgumentException("필수 값이 누락되었습니다.");
        }
        if (req.senderId().equals(req.recipientId())) {
            throw new IllegalArgumentException("본인에게는 보낼 수 없습니다.");
        }
        if (loveCallRepository.existsByRecruitmentIdAndSenderIdAndRecipientId(
                req.recruitmentId(), req.senderId(), req.recipientId())) {
            throw new IllegalStateException("이미 동일 대상에게 보낸 러브콜이 있습니다.");
        }

        LoveCall lc = LoveCall.builder()
                .recruitmentId(req.recruitmentId())
                .senderId(req.senderId())
                .recipientId(req.recipientId())
                .message(req.message())
                .build();

        LoveCall saved = loveCallRepository.save(lc);
        return toDto(saved);
    }

    /** 받은 러브콜 목록 (마이페이지 수신함) */
    @Transactional(readOnly = true)
    public Page<LoveCallResponse> listReceived(Long recipientId, Pageable pageable) {
        if (recipientId == null) throw new IllegalArgumentException("recipientId가 필요합니다.");
        return loveCallRepository
                .findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable)
                .map(this::toDto);
    }

    /** 보낸 러브콜 목록 (선택) */
    @Transactional(readOnly = true)
    public Page<LoveCallResponse> listSent(Long senderId, Pageable pageable) {
        if (senderId == null) throw new IllegalArgumentException("senderId가 필요합니다.");
        return loveCallRepository
                .findBySenderIdOrderByCreatedAtDesc(senderId, pageable)
                .map(this::toDto);
    }

    /** 상세 조회(수신자 권한) + 읽음 처리 옵션 */
    @Transactional
    public LoveCallResponse getDetailForRecipient(Long loveCallId, Long recipientId, boolean markRead) {
        LoveCall lc = loveCallRepository.findByIdAndRecipientId(loveCallId, recipientId)
                .orElseThrow(() -> new IllegalArgumentException("러브콜을 찾을 수 없습니다."));
        if (markRead) {
            lc.markRead(); // 엔티티에 readAt 설정하는 메서드가 있다고 가정
        }
        return toDto(lc);
    }

    /** 상세 조회(발신자 권한) */
    @Transactional(readOnly = true)
    public LoveCallResponse getDetailForSender(Long loveCallId, Long senderId) {
        LoveCall lc = loveCallRepository.findByIdAndSenderId(loveCallId, senderId)
                .orElseThrow(() -> new IllegalArgumentException("러브콜을 찾을 수 없습니다."));
        return toDto(lc);
    }

    /** 미읽음 카운트(헤더 뱃지 용도) */
    @Transactional(readOnly = true)
    public long unreadCount(Long recipientId) {
        if (recipientId == null) throw new IllegalArgumentException("recipientId가 필요합니다.");
        return loveCallRepository.countByRecipientIdAndReadAtIsNull(recipientId);
    }

    /** 명시적 읽음 처리 */
    @Transactional
    public void markRead(Long loveCallId, Long recipientId) {
        LoveCall lc = loveCallRepository.findByIdAndRecipientId(loveCallId, recipientId)
                .orElseThrow(() -> new IllegalArgumentException("러브콜을 찾을 수 없습니다."));
        lc.markRead();
    }

    // ─────────────────────────────────────────────

    private LoveCallResponse toDto(LoveCall lc) {
        return new LoveCallResponse(
                lc.getId(),
                lc.getRecruitmentId(),
                lc.getRecipientId(),
                lc.getSenderId(),
                lc.getMessage(),
                lc.getCreatedAt(),
                lc.getReadAt()
        );
    }

    @Transactional
    public void deleteForUser(Long loveCallId, Long userId) {
        LoveCall lc = loveCallRepository.findById(loveCallId)
                .orElseThrow(() -> new IllegalArgumentException("러브콜을 찾을 수 없습니다."));

        // 권한 체크: 발신자/수신자 외엔 삭제 불가
        if (!userId.equals(lc.getSenderId()) && !userId.equals(lc.getRecipientId())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        loveCallRepository.delete(lc); // ★ 물리 삭제
    }
    @Transactional
    public void deleteManyForUser(java.util.List<Long> ids, Long userId) {
        for (Long id : ids) {
            deleteForUser(id, userId);
        }
    }

    @Transactional
    public void deleteAsSender(Long loveCallId, Long senderId) {
        LoveCall lc = loveCallRepository.findById(loveCallId)
                .orElseThrow(() -> new IllegalArgumentException("러브콜을 찾을 수 없습니다."));

        if (!lc.getSenderId().equals(senderId)) {
            throw new IllegalArgumentException("본인이 보낸 러브콜만 삭제할 수 있습니다.");
        }
        loveCallRepository.delete(lc);
    }
}
