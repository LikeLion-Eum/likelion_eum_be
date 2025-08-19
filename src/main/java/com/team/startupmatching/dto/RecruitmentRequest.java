// src/main/java/com/team/startupmatching/dto/request/RecruitmentRequest.java
package com.team.startupmatching.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitmentRequest {
    private String title;       // 제목
    private String location;    // 지역
    private String position;    // 직무
    private String skills;      // 기술
    private String career;      // 경력
    private Long recruitCount;  // 모집 인원(Long 통일)
    private String content;     // 상세 내용
    private Boolean isClosed;   // 마감 여부
    private Long userId;        // 작성자 FK
}
