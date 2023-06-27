package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HospitalBedAvailabilityResponse {
    private int totalBeds;
    private int bedsInUse;
    private int availableBeds;
}
