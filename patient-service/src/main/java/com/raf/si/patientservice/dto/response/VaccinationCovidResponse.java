package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationCovidResponse {
    private Long id;
    private String vaccine;
    private Long doseReceived;
    private ScheduledVaccinationResponse scheduledVaccinationResponse;
    private LocalDateTime localDateTime;

}
