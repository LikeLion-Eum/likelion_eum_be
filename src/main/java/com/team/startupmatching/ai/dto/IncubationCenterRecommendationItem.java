package com.team.startupmatching.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IncubationCenterRecommendationItem(
        int rank,
        String title,
        String region,
        @JsonProperty("support_field") String supportField,
        @JsonProperty("end_date") java.time.LocalDate endDate,
        @JsonProperty("apply_url") String applyUrl,
        String reason
) {}
