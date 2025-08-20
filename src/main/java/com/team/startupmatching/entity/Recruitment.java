package com.team.startupmatching.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
@AllArgsConstructor
@Builder
@Entity
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(nullable = false)
    private String title;       // 제목 (필수)

    private String location;    // 지역 (선택)
    private String position;    // 직무 (선택)
    private String skills;      // 기술 (선택)
    private String career;      // 경력 (선택)

    @Column(nullable = false)
    private Long recruitCount;  // 모집 인원 (필수)

    @Column(columnDefinition = "TEXT")
    private String content;     // 상세 내용 (선택, TEXT)

    @Column(nullable = false)
    private Boolean isClosed;   // 마감 여부 (필수, 기본 false)

    @Column(nullable = false)
    private LocalDateTime createdAt; // 등록 일시 (필수)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;          // 작성자 (필수)

    @PrePersist
    void onCreate() {
        if (isClosed == null) isClosed = false;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
