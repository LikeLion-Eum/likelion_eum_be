package com.team.startupmatching.service;

import com.team.startupmatching.entity.LoveCall;
import com.team.startupmatching.repository.LoveCallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoveCallService {

    private final LoveCallRepository loveCallRepository;

    /** 러브콜 보내기 (중복 방지) */
    @Transactional
    public LoveCall send(Long recruitmentId, Long senderId, Long recipientId, String message) {
        if (recruitmentId == null || senderId == null || recipientId == null) {
            throw new IllegalArgumentException("필수 값이 누락되었습니다.");
        }
        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("본인에게는 보낼 수 없습니다.");
        }
        if (loveCallRepository.existsByRecruitmentIdAndSenderIdAndRecipientId(recruitmentId, senderId, recipientId)) {
            throw new IllegalStateException("이미 동일 대상에게 보낸 러브콜이 있습니다.");
        }

        LoveCall lc = LoveCall.builder()
                .recruitmentId(recruitmentId)
                .senderId(senderId)
                .recipientId(recipientId)
                .message(message)
                .build();

        return loveCallRepository.save(lc);
    }

    /** 받은 러브콜 목록 (마이페이지 수신함) */
    @Transactional(readOnly = true)
    public Page<LoveCall> listReceived(Long recipientId, Pageable pageable) {
        return loveCallRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);
    }

    /** 보낸 러브콜 목록 (선택) */
    @Transactional(readOnly = true)
    public Page<LoveCall> listSent(Long senderId, Pageable pageable) {
        return loveCallRepository.findBySenderIdOrderByCreatedAtDesc(senderId, pageable);
    }

    /** 상세 조회(수신자 권한) + 읽음 처리 옵션 */
    @Transactional
    public LoveCall getDetailForRecipient(Long loveCallId, Long recipientId, boolean markRead) {
        LoveCall lc = loveCallRepository.findByIdAndRecipientId(loveCallId, recipientId)
                .orElseThrow(() -> new IllegalArgumentException("러브콜을 찾을 수 없습니다."));
        if (markRead) {
            lc.markRead();
        }
        return lc;
    }

    /** 상세 조회(발신자 권한) */
    @Transactional(readOnly = true)
    public LoveCall getDetailForSender(Long loveCallId, Long senderId) {
        return loveCallRepository.findByIdAndSenderId(loveCallId, senderId)
                .orElseThrow(() -> new IllegalArgumentException("러브콜을 찾을 수 없습니다."));
    }

    /** 미읽음 카운트(헤더 뱃지 용도) */
    @Transactional(readOnly = true)
    public long unreadCount(Long recipientId) {
        return loveCallRepository.countByRecipientIdAndReadAtIsNull(recipientId);
    }

    /** 명시적 읽음 처리 */
    @Transactional
    public void markRead(Long loveCallId, Long recipientId) {
        LoveCall lc = loveCallRepository.findByIdAndRecipientId(loveCallId, recipientId)
                .orElseThrow(() -> new IllegalArgumentException("러브콜을 찾을 수 없습니다."));
        lc.markRead();
    }
}
