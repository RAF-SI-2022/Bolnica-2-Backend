package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.request.VaccinationCovidRequest;
import com.raf.si.patientservice.dto.response.DosageReceivedResponse;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationListResponse;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationResponse;
import com.raf.si.patientservice.dto.response.VaccinationCovidResposne;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.UUID;

public interface VaccinationCovidService {
    ScheduledVaccinationResponse scheduleVaccination(UUID lbp, ScheduledVaccinationRequest request, String authorizationHeader);

    ScheduledVaccinationListResponse getScheduledVaccinations(UUID lbp, LocalDate date, Pageable pageable);

    VaccinationCovidResposne createVaccination(UUID lbp, VaccinationCovidRequest request, String token);

    DosageReceivedResponse getPatientDosageReceived(UUID lbp);

    ScheduledVaccinationResponse changeScheduledVaccinationStatus(Long scheduledVaccinationId, String vaccStatus, String patientArrivalStatus);

    ScheduledVaccinationResponse deleteScheduledVaccination(Long id);
}
