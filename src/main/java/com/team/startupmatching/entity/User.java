package com.team.startupmatching.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Setter
@Table(name = "`user`")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(nullable = false)
    private String name; // 이름 (필수)

    @Column(nullable = false)
    private String email; // 이메일 (필수)

    @Column(columnDefinition = "TEXT")
    private String introduction; // 자기소개 (길이 제한 없음, 선택)

    @Column
    private String skills; // 기술 스택 (선택)

    @Column
    private String career; // 경력 (선택)

    @Column(nullable = false)
    private String location; // 지역 (필수)

    @Column
    private String resumeUrl; // 이력서 링크 (선택)

    @Column // 처음엔 nullable 허용(기존 행 때문). 값은 콜백에서 채움
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}