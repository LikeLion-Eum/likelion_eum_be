package com.team.startupmatching.repository;

import com.team.startupmatching.entity.LoveCall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoveCallRepository extends JpaRepository<LoveCall, Long> {

    // 받은 러브콜 (마이페이지 수신함)
    Page<LoveCall> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    // 보낸 러브콜 (보낸함 - 선택)
    Page<LoveCall> findBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);

    // 헤더 뱃지용 미읽음 개수
    long countByRecipientIdAndReadAtIsNull(Long recipientId);

    // 상세 조회(권한검사용)
    Optional<LoveCall> findByIdAndRecipientId(Long id, Long recipientId);
    Optional<LoveCall> findByIdAndSenderId(Long id, Long senderId);

    // 중복 전송 방지(같은 모집글에서 같은 대상에게)
    boolean existsByRecruitmentIdAndSenderIdAndRecipientId(Long recruitmentId, Long senderId, Long recipientId);
}