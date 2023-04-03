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
    @NotNull(message = "Start date can't be null.")
    private Date startDate;
    @NotNull(message = "End date can't be null.")

    private Date endDate;
    @NotNull(message = "LBP can't be null.")

    private UUID lbp;
}
