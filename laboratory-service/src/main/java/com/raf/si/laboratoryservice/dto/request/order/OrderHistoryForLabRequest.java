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
    private Date startDate;
    private Date endDate;
    private UUID lbp;
    private String orderStatus;
}
