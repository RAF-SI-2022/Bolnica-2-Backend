package com.raf.si.laboratoryservice.dto.response.order;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderResponse {
    String message;

    public CreateOrderResponse(String message) {
        this.message = message;
    }
}
