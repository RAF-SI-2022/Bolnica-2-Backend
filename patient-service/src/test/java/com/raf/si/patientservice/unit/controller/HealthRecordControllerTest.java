package com.raf.si.patientservice.unit.controller;

import com.raf.si.patientservice.controller.HealthRecordController;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.LightHealthRecordResponse;
import com.raf.si.patientservice.dto.response.MedicalExaminationListResponse;
import com.raf.si.patientservice.dto.response.MedicalHistoryListResponse;
import com.raf.si.patientservice.service.HealthRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HealthRecordControllerTest {

    @Mock
    private HealthRecordService healthRecordService;

    @InjectMocks
    private HealthRecordController healthRecordController;

    @Test
    void getHealthRecordForPatient_Success(){
        HealthRecordResponse response = new HealthRecordResponse();

        when(healthRecordService.getHealthRecordForPatient(any(), any()))
                .thenReturn(response);

        assertEquals(healthRecordController.getHealthRecordForPatient(null, 0, 5),
                ResponseEntity.ok(response));
    }

    @Test
    void getLightHealthRecordForPatient_Success(){
        LightHealthRecordResponse response = new LightHealthRecordResponse();

        when(healthRecordService.getLightHealthRecordForPatient(any(), any()))
                .thenReturn(response);

        assertEquals(healthRecordController.getLightHealthRecordResponse(null, 0, 5),
                ResponseEntity.ok(response));
    }

    @Test
    void getExaminationsForPatient_Success(){
        MedicalExaminationListResponse response = new MedicalExaminationListResponse();

        when(healthRecordService.findExaminations(any(), any(), any()))
                .thenReturn(response);

        assertEquals(healthRecordController.getExaminations(null, null, 0, 5),
                ResponseEntity.ok(response));
    }

    @Test
    void getMedicalHistoryForPatient_Success(){
        MedicalHistoryListResponse response = new MedicalHistoryListResponse();

        when(healthRecordService.findMedicalHistory(any(), any(), any()))
                .thenReturn(response);

        assertEquals(healthRecordController.getMedicalHistory(null, null, 0, 5),
                ResponseEntity.ok(response));
    }
}
