package com.raf.si.patientservice.unit.controller;

import com.raf.si.patientservice.controller.VaccinationCovidController;
import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationResponse;
import com.raf.si.patientservice.service.VaccinationCovidService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VaccinationCovidControllerTest {

    @Mock
    private VaccinationCovidService vaccinationCovidService;
    @InjectMocks
    private VaccinationCovidController vaccinationCovidController;


    @Test
    void scheduleVaccination_Success(){
        UUID lbp = UUID.randomUUID();
        ScheduledVaccinationRequest request = makeScheduledTestingRequest();
        ScheduledVaccinationResponse response = new ScheduledVaccinationResponse();

        when(vaccinationCovidService.scheduleVaccination(lbp,request,""))
                .thenReturn(response);

        assertEquals(vaccinationCovidController.scheduleVaccination(lbp,request,"")
                , ResponseEntity.ok(response));
    }




    private ScheduledVaccinationRequest makeScheduledTestingRequest(){
        ScheduledVaccinationRequest scheduledVaccinationRequest = new ScheduledVaccinationRequest();
        scheduledVaccinationRequest.setDateAndTime(LocalDateTime.now());
        return  scheduledVaccinationRequest;
    }
}
