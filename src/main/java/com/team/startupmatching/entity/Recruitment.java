package com.team.startupmatching.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // 모집글 고유 ID (PK)

    private String title;              // 모집글 제목
    private String content;            // 모집글 내용 (설명)
    private String writer;             // 작성자 (사용자 이름 또는 닉네임)
    private String contact;            // 연락처 (전화번호, 이메일 등)

    private String spaceName;          // 모집하려는 공간 이름 (예: 카페 A)
    private String spaceLocation;      // 공간 위치 (예: 충남 아산시 신창면)

    private LocalDateTime createdAt;   // 모집글 등록 시각
}
