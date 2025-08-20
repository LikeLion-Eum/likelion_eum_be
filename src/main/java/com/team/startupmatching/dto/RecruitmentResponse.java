package com.team.startupmatching.dto;

import com.team.startupmatching.entity.Recruitment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentResponse {

    private Long id;               // PK
    private String title;          // 제목
    private String location;       // 지역
    private String position;       // 직무
    private String skills;         // 기술
    private String career;         // 경력
    private Long recruitCount;     // 모집 인원 수
    private String content;        // 상세 내용
    private Boolean isClosed;      // 마감 여부
    private LocalDateTime createdAt; // 등록 일시
    private Long userId;           // 작성자 FK

    public static RecruitmentResponse from(Recruitment r) {
        return RecruitmentResponse.builder()
                .id(r.getId())
                .title(r.getTitle())
                .location(r.getLocation())
                .position(r.getPosition())
                .skills(r.getSkills())
                .career(r.getCareer())
                .recruitCount(r.getRecruitCount())
                .content(r.getContent())
                .isClosed(r.getIsClosed())          // 필드명이 isClosed라면 getter는 getIsClosed()
                .createdAt(r.getCreatedAt())
                .userId(r.getUser() != null ? r.getUser().getId() : null)
                .build();
    }
}
