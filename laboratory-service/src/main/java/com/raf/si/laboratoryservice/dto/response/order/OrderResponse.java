package com.raf.si.laboratoryservice.dto.response.order;

import com.raf.si.laboratoryservice.model.AnalysisParameterResult;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Date creationTime;
    private OrderStatus status;
    private Long referralId;

    private List<AnalysisResultResponse> analysisParameterResults;
    private UUID lbp;
    private UUID lbzTechnician;
    private UUID lbzBiochemist;
}
