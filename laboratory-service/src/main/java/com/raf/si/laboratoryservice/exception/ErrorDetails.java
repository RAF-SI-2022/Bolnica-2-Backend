package com.raf.si.laboratoryservice.exception;

import lombok.Getter;

@Getter
public class ErrorDetails {

    private final ErrorCode errorCode;
    private final String errorMessage;
    private final String timestamp;

    public ErrorDetails(ErrorCode errorCode, String errorMessage, String timestamp) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }
}
