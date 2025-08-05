package com.team.startupmatching.exception;

public class RecruitmentException extends RuntimeException {

    private final String errorCode;

    public RecruitmentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
