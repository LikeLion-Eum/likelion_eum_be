package com.team.startupmatching.dto;

import java.time.LocalDateTime;

public record LoveCallResponse(
        Long id,
        Long recruitmentId,
        Long recipientId,
        Long senderId,
        String message,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {}