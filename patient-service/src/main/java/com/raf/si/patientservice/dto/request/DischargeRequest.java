package com.raf.si.patientservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
@Setter
public class DischargeRequest {

    private String attendDiagnoses;
    @NotEmpty(message = "Polje anamneza ne moze biti prazno")
    private String anamnesis;
    private String courseDisease;
    @NotEmpty(message = "Polje zakljucak ne moze biti prazno")
    private String conclusion;
    private String therapy;
}
