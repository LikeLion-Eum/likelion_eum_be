package com.team.startupmatching.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IncubationCenterRecommendationItem(
        int rank,
        String title,
        String region,
        @JsonProperty("support_field") String supportField,
        @JsonProperty("end_date") String endDate,        // ← LocalDate → String 로 변경
        @JsonProperty("apply_url") String applyUrl,
        String reason
) {}