package com.raf.si.laboratoryservice.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class SaveResultRequest {
    @NotNull(message = "OrderID can't be null.")
    private Long orderId;
    @NotNull(message = "ParameterID can't be null.")
    private Long parameterId;
    @NotNull(message = "Result can't be null.")
    private String result;
}
