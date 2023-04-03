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
    @NotNull(message = "Start date can't be null.")
    private Date startDate;
    @NotNull(message = "End date can't be null.")
    private Date endDate;
    @NotNull(message = "LBP can't be null.")
    private UUID lbp;
    @NotEmpty(message = "OrderStatus can't be empty.")
    private String orderStatus;
}
