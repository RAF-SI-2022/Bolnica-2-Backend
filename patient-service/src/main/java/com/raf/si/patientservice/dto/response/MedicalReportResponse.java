package com.raf.si.patientservice.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class MedicalReportResponse {

    private Long id;
    private UUID lbp;
    private UUID doctorLbz;
    private Date date;
    private Boolean confidentIndicator;
    private String objectiveResult;
    private String diagnosis;
    private String proposedTherapy;
    private String advice;
}
