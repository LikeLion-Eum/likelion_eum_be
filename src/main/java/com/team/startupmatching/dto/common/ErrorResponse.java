package com.team.startupmatching.dto.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private int status;         // HTTP 상태 코드
    private String message;     // 사용자에게 보여줄 메시지
    private String errorCode;   // 내부 에러 코드 (예: "RECRUITMENT_TITLE_MISSING")
}