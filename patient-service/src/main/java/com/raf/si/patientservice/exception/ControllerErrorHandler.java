package com.raf.si.patientservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;

@ControllerAdvice
public class ControllerErrorHandler {

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleCustomException(CustomException exception) {
        ErrorDetails errorDetails = new ErrorDetails(exception.getErrorCode(), exception.getMessage(), Instant.now().toString());
        return new ResponseEntity<>(errorDetails, exception.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetails> handleNotValidException(MethodArgumentNotValidException exception) {
        StringBuilder message = new StringBuilder();
        for (ObjectError error : exception.getAllErrors()) {
            message.append(error.getDefaultMessage());
            message.append(";");
        }
        ErrorDetails errorDetails = new ErrorDetails(ErrorCode.BAD_REQUEST, message.toString(), Instant.now().toString());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}