package com.raf.si.patientservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class UpdateHealthRecordRequest {

    @NotEmpty(message = "Polje krvna grupa ne sme da bude prazno")
    private String blodtype;

    @NotEmpty(message = "Polje Rh faktor ne sme da bude prazno")
    private String rhfactor;

}
