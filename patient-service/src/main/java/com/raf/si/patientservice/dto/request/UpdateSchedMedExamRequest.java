package com.raf.si.patientservice.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateSchedMedExamRequest {

    @NotNull(message= "Potrebno je proslediti id zakazanog pregleda")
    private Long id;
    @NotEmpty(message = "Novi status pregleda ne sme biti prazan")
    private String examinationStatus;


}
