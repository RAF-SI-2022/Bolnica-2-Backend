package com.raf.si.laboratoryservice.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class SaveResultRequest {
    @NotNull(message = "ID naloga ne može biti null.")
    private Long orderId;
    @NotNull(message = "ID parametra ne može biti null.")
    private Long parameterId;
    @NotNull(message = "Rezultat ne može biti null.")
    private String result;
}
