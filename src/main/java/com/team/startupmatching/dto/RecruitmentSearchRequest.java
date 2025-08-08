package com.team.startupmatching.dto;


import com.team.startupmatching.dto.common.SpaceType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RecruitmentSearchRequest {
    private String keyword;
    private List<String> keywords;// 옵션
    private SpaceType targetSpaceType;   // 옵션: SHARED_OFFICE | INCUBATION_CENTER
}