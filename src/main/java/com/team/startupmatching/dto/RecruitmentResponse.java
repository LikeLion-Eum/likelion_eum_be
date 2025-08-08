package com.team.startupmatching.dto;



import com.team.startupmatching.dto.common.SpaceType;
import com.team.startupmatching.entity.Recruitment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentResponse {

    private SpaceType targetSpaceType;

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

    public static RecruitmentResponse from(Recruitment r){
        return builder()
                // ...
                .targetSpaceType(r.getTargetSpaceType())
                .build();
    }
}