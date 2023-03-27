package com.raf.si.patientservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AddVaccinationRequest {

    @NotEmpty(message = "Polje ime vakcine ne sme da bude prazno")
    private String vaccine;

    @NotEmpty(message = "Polje datum vakcinacije ne sme da bude prazno")
    private Date date;


}
