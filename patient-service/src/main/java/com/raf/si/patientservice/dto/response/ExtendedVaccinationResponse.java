package com.raf.si.patientservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExtendedVaccinationResponse {
    private VaccinationResponse vaccinationResponse;
    private Long vaccinationCount;
}
