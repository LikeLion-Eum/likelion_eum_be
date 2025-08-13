package com.team.startupmatching.ai.dto;

public record RecommendRequest(
        String title,
        String position,
        String location,
        String skills,
        String career,
        String content
) {}