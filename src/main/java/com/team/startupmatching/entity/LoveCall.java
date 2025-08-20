package com.team.startupmatching.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "love_call",
        indexes = {
                @Index(name = "idx_lc_recipient_created", columnList = "recipient_id,created_at"),
                @Index(name = "idx_lc_sender_created", columnList = "sender_id,created_at"),
                @Index(name = "idx_lc_recruitment", columnList = "recruitment_id")
        }
)
public class LoveCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 어떤 모집글에서 보냈는지 (연관관계 없이 ID만 저장해서 결합도↓) */
    @Column(name = "recruitment_id", nullable = false)
    private Long recruitmentId;

    /** 보낸 사람(모집글 작성자) */
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    /** 받은 사람(추천된 유저) */
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    /** 보낸 메시지(연락처/이메일 포함 문구 유도) */
    @Column(name = "message", length = 1000)
    private String message;

    /** 읽은 시각(null이면 미열람) */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /** 읽음 처리 헬퍼 */
    public void markRead() {
        if (this.readAt == null) this.readAt = LocalDateTime.now();
    }
}
