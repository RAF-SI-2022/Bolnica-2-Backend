package com.raf.si.laboratoryservice.dto.response.order;

import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ResultResponse {
    private List<AnalysisResultResponse> results;
    private Long orderId;
    private Date orderCreationTime;
    private OrderStatus orderStatus;
    private Long referralId;
    private UUID lbp;
    private UUID lbzTechnician;
}
