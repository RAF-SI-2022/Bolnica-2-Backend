package com.raf.si.patientservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class AddAllergyRequest {

    @NotEmpty(message = "Polje allergen ne sme da bude prazno")
    private String allergen;

}
