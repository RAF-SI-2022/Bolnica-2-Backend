package com.raf.si.laboratoryservice.dto.request.order;

import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class OrderHistoryForLabRequest {
    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;
    @NotNull
    private UUID lbp;
    @NotNull
    private OrderStatus orderStatus;
}
