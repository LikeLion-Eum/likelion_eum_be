package com.team.startupmatching.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UserRecommendationItem(
        int rank,
        @JsonProperty("user_id") Long userId,           // AI가 snake_case로 주면 매핑
        String name,
        String career,
        @JsonProperty("main_skills") List<String> mainSkills,
        String reason
) {}
