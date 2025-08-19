package com.team.startupmatching.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecruitmentSearchRequest {
    private String keyword;
    private List<String> keywords; // 옵션
}