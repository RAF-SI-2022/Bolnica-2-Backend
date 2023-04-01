package com.raf.si.patientservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExtendedAllergyResponse {
    private AllergyResponse allergyResponse;
    private Long allergyCount;
}
