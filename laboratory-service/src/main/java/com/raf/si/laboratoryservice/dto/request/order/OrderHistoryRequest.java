package com.raf.si.laboratoryservice.dto.request.order;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class OrderHistoryRequest {
    private Date startDate;
    private Date endDate;
    private UUID lbp;
}
