package com.raf.si.patientservice.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
public class MedicalExaminationRequest {
    @NotNull(message = "Morate uneti lbp pacijenta")
    private UUID lbp;
    private Date startDate;
    private Date endDate;
}
