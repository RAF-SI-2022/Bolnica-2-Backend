package com.raf.si.patientservice.unit.controller;

import com.raf.si.patientservice.controller.VaccinationCovidController;
import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.request.VaccinationCovidRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.service.CovidCertificateService;
import com.raf.si.patientservice.service.VaccinationCovidService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VaccinationCovidControllerTest {

    @Mock
    private VaccinationCovidService vaccinationCovidService;
    @Mock
    private CovidCertificateService covidCertificateService;
    @InjectMocks
    private VaccinationCovidController vaccinationCovidController;

    @Test
    void scheduleVaccination_Success() {
        UUID lbp = UUID.randomUUID();
        ScheduledVaccinationRequest request = makeScheduledTestingRequest();
        ScheduledVaccinationResponse response = new ScheduledVaccinationResponse();

        when(vaccinationCovidService.scheduleVaccination(lbp, request, ""))
                .thenReturn(response);

        assertEquals(vaccinationCovidController.scheduleVaccination(lbp, request, "")
                , ResponseEntity.ok(response));
    }

    @Test
    void getScheduledTestings_Success() {
        ScheduledVaccinationListResponse response = new ScheduledVaccinationListResponse();
        UUID lbp = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        when(vaccinationCovidService.getScheduledVaccinations(lbp, date, PageRequest.of(0, 1)))
                .thenReturn(response);

        assertEquals(vaccinationCovidController.getScheduledVaccinations(lbp, date, 0, 1)
                , ResponseEntity.ok(response));
    }

    @Test
    void createVaccination_Success() {
        UUID lbp = UUID.randomUUID();
        VaccinationCovidRequest request = makeVaccinationCovidRequest();
        VaccinationCovidResponse response = new VaccinationCovidResponse();

        when(vaccinationCovidService.createVaccination(lbp, request, ""))
                .thenReturn(response);

        assertEquals(vaccinationCovidController.createVaccination("", request, lbp)
                , ResponseEntity.ok(response));

    }

    @Test
    void getPatientDosageReceived_Success() {
        UUID lbp = UUID.randomUUID();
        DosageReceivedResponse response = new DosageReceivedResponse();

        when(vaccinationCovidService.getPatientDosageReceived(lbp))
                .thenReturn(response);

        assertEquals(vaccinationCovidController.getPatientDosageReceived(lbp)
                , ResponseEntity.ok(response));
    }

    @Test
    void changeVaccinationStatus_Success() {
        ScheduledVaccinationResponse response = new ScheduledVaccinationResponse();

        when(vaccinationCovidService.changeScheduledVaccinationStatus(1L, "", ""))
                .thenReturn(response);

        assertEquals(vaccinationCovidController.changeVaccinationStatus(1L, "", "")
                , ResponseEntity.ok(response));
    }

    @Test
    void deleteScheduledVaccination_Success() {
        ScheduledVaccinationResponse response = new ScheduledVaccinationResponse();

        when(vaccinationCovidService.deleteScheduledVaccination(1L))
                .thenReturn(response);

        assertEquals(vaccinationCovidController.deleteScheduledVaccination(1L)
                , ResponseEntity.ok(response));
    }

    @Test
    void getVaccinationCovidHistory_Success() {
        List<VaccinationCovidResponse> vaccinationCovidResponses = new ArrayList<>();
        when(vaccinationCovidService.getVaccinationCovidHistory(any()))
                .thenReturn(vaccinationCovidResponses);

        assertEquals(vaccinationCovidController.getVaccinationCovidHistory(UUID.randomUUID()).getBody(),
                vaccinationCovidResponses);
    }

    @Test
    void getCovidCertificatesHistory_Success() {
        CovidCertificateResponse covidCertificateResponse = new CovidCertificateResponse();
        List<CovidCertificateResponse> covidCertificateResponseList = Collections.singletonList(covidCertificateResponse);

        when(covidCertificateService.getCovidCertificateHistory(any(), any(), any()))
                .thenReturn(covidCertificateResponseList);

        assertEquals(vaccinationCovidController.getCovidCertificatesHistory(any(), any(), any()).getBody(),
                covidCertificateResponseList);
    }

    private VaccinationCovidRequest makeVaccinationCovidRequest() {
        VaccinationCovidRequest request = new VaccinationCovidRequest();
        request.setVaccinationId(1L);
        request.setVaccineName("SARS");
        request.setDateTime(LocalDateTime.now());
        return request;
    }

    private ScheduledVaccinationRequest makeScheduledTestingRequest() {
        ScheduledVaccinationRequest scheduledVaccinationRequest = new ScheduledVaccinationRequest();
        scheduledVaccinationRequest.setDateAndTime(LocalDateTime.now());
        return scheduledVaccinationRequest;
    }
}
