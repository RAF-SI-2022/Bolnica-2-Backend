package com.raf.si.laboratoryservice.dto.response.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderResponse {
    String message;

    public CreateOrderResponse(String message) {
        this.message = message;
    }
}
