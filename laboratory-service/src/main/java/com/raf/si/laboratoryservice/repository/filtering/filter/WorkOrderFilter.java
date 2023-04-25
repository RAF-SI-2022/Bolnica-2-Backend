package com.raf.si.laboratoryservice.repository.filtering.filter;

import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderFilter {
    private UUID lbp;
    private Date startDate;
    private Date endDate;
    private OrderStatus orderStatus;
}
