package com.raf.si.laboratoryservice.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class VerifyRequest {
    @NotNull
    private Long orderId;
}
