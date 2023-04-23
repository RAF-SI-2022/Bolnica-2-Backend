package com.raf.si.laboratoryservice.dto.response.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyResponse {
    private String message;

    public VerifyResponse(String message) {
        this.message = message;
    }
}
