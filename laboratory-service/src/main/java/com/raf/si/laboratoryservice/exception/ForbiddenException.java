package com.raf.si.laboratoryservice.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends CustomException {

    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
}
