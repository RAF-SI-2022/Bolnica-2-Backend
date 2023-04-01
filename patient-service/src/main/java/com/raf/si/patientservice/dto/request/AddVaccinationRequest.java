package com.raf.si.patientservice.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Date;


@NoArgsConstructor
@Data
public class AddVaccinationRequest {

    @NotEmpty(message = "Polje ime vakcine ne sme da bude prazno")
    private String vaccine;

    private Date date;


}
