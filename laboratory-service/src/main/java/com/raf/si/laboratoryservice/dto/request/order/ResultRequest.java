package com.raf.si.laboratoryservice.dto.request.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ResultRequest {
    @NotNull
    Long orderId;
}
