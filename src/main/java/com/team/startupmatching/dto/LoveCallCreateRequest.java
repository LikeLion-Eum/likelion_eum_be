package com.team.startupmatching.dto;

import jakarta.validation.constraints.*;

public record LoveCallCreateRequest(
        @NotNull Long recruitmentId,
        @NotNull Long recipientId,
        @NotNull Long senderId,
        @Size(max = 1000) String message
) {}