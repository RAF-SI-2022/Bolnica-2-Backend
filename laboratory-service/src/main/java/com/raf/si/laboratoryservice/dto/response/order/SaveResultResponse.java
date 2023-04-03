package com.raf.si.laboratoryservice.dto.response.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveResultResponse {
    private String message;

    public SaveResultResponse(String message) {
        this.message = message;
    }
}
