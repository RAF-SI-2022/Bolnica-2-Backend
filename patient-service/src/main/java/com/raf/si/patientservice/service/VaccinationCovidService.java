package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationResponse;


import java.util.UUID;

public interface VaccinationCovidService {
    ScheduledVaccinationResponse scheduleVaccination(UUID lbp, ScheduledVaccinationRequest request, String authorizationHeader);
}
