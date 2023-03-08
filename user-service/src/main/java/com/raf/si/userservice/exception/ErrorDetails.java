package com.raf.si.userservice.exception;

public class ErrorDetails {

    private final ErrorCode errorCode;
    private final String errorMessage;
    private final String timestamp;

    public ErrorDetails(ErrorCode errorCode, String errorMessage, String timestamp) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
