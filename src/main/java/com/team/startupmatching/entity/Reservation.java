package com.team.startupmatching.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "reservation",
        indexes = {
                @Index(name = "idx_resv_office_start", columnList = "shared_office_id,start_at")
        }
)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 오피스의 예약인지 (필수)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shared_office_id", nullable = false)
    private SharedOffice sharedOffice;

    // 예약자 정보
    @Column(name = "reserver_name", nullable = false, length = 50)
    private String reserverName;

    @Column(name = "reserver_phone", nullable = false, length = 30)
    private String reserverPhone; // 숫자만 저장(서비스에서 정규화), 타입은 String 유지

    @Column(name = "reserver_email", nullable = false, length = 100)
    private String reserverEmail;

    // 이용 기간
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;     // 시작 일시

    @Column(name = "months", nullable = false)
    private Long months;               // 몇 개월 (>= 1)  ← Integer → Long 변경

    // 선택 입력
    @Column(name = "inquiry_note", columnDefinition = "TEXT")
    private String inquiryNote;        // 문의 사항(옵션)

    // 타임스탬프
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
