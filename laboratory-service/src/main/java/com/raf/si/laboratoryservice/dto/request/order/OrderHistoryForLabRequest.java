package com.raf.si.laboratoryservice.dto.request.order;

import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class OrderHistoryForLabRequest {
    @NotNull(message = "Početni datum ne može biti null.")
    private Date startDate;
    @NotNull(message = "Krajnji datum ne može biti null.")
    private Date endDate;
    @NotNull(message = "LBP ne može null.")
    private UUID lbp;
    @NotEmpty(message = "Status naloga ne može biti null.")
    private String orderStatus;
}
