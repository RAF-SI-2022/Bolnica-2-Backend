package com.raf.si.patientservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
@Setter
public class MedicalReportRequest {

    private Boolean confidentIndicator;
    @NotEmpty(message = "Objektivni nalaz ne moze biti prazan")
    private String objectiveResult;
    private String diagnosis;
    private String proposedTherapy;
    private String advice;
}
