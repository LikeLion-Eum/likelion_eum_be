package com.team.startupmatching.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitmentUpdateRequest {

    // 기본 정보
    private String title;
    private String location;     // "서울 강남구" 같은 한 줄 주소/권역
    private String position;     // 직무/포지션
    private String skills;       // "React, TypeScript" 같은 CSV
    private String career;       // 경력/신입/무관
    private Long recruitCount;   // 모집 인원
    private String content;      // 상세 내용 (HTML/Markdown 등)
    private Boolean isClosed;    // 마감 여부

    // 작성자(필요 시)
    private Long userId;         // 작성자 FK (변경 허용할지 정책에 맞게)
}
