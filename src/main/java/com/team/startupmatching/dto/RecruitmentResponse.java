package com.team.startupmatching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class RecruitmentResponse {

    private Long id;                   // 모집글 고유 ID
    private String title;             // 모집글 제목
    private String content;           // 모집글 본문 내용
    private String writer;            // 작성자 이름
    private String contact;           // 연락처 정보
    private String spaceName;         // 모집 관련 공간 이름
    private String spaceLocation;     // 공간 위치 정보
    private LocalDateTime createdAt;  // 모집글 등록 시각


}
