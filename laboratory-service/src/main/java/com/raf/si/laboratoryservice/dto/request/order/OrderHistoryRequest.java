package com.raf.si.laboratoryservice.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class OrderHistoryRequest {
    @NotNull(message = "Početni datum ne može biti null.")
    private Date startDate;
    @NotNull(message = "Krajnji datum ne može biti null.")
    private Date endDate;
    @NotNull(message = "LBP ne može biti null.")
    private UUID lbp;
}
