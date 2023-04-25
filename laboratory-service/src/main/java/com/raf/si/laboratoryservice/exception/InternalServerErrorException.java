package com.raf.si.laboratoryservice.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends CustomException {

    public InternalServerErrorException(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
