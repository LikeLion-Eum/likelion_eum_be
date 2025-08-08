package com.team.startupmatching.dto;

import com.team.startupmatching.dto.common.SpaceType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
/**
 * 모집글 등록/수정 요청 DTO
 * 엔티티 필드와 1:1 매핑 (+ userId는 FK 식별자)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentRequest {

    private String title;       // 제목 (필수)
    private String location;    // 지역
    private String position;    // 직무
    private String skills;      // 기술
    private String career;      // 경력
    private Long recruitCount;  // 모집 인원 수  (프로젝트 기준 Long로 통일)
    private String content;     // 상세 내용
    private Boolean isClosed;   // 마감 여부
    private Long userId;        // 작성자 FK (필수)

    @NotNull
    private SpaceType targetSpaceType; // ✅ 드롭다운 값
}