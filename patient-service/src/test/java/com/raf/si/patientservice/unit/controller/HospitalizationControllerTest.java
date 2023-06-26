package com.raf.si.patientservice.unit.controller;

import com.raf.si.patientservice.controller.HospitalizationController;
import com.raf.si.patientservice.dto.request.DischargeRequest;
import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.request.MedicalReportRequest;
import com.raf.si.patientservice.dto.request.PatientConditionRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.service.HospitalizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HospitalizationControllerTest {

    @Mock
    private HospitalizationService hospitalizationService;

    @InjectMocks
    private HospitalizationController hospitalizationController;

    @Test
    void hospitalize_Success() {
        HospitalizationRequest request = new HospitalizationRequest();
        HospitalizationResponse response = new HospitalizationResponse();

        when(hospitalizationService.hospitalize(request, ""))
                .thenReturn(response);

        assertEquals(hospitalizationController.hospitalization(request, ""),
                ResponseEntity.ok(response));
    }

    @Test
    void getHospitalisedPatients() {
        HospitalisedPatientsListResponse response = new HospitalisedPatientsListResponse(new ArrayList<>(), 0L);
        UUID pbo = UUID.randomUUID();

        when(hospitalizationService.getHospitalisedPatients(
                null, pbo, null, null, null, null, PageRequest.of(0, 5))
        ).thenReturn(response);

        assertEquals(hospitalizationController.getHospitalisedPatients(pbo, null,
                null, null,
                null, 0,
                5, null)
                        .getBody(),
                response);
    }

    @Test
    void getHospitalisedPatientsByHospital() {
        HospPatientByHospitalListResponse response = new HospPatientByHospitalListResponse(new ArrayList<>(), 0L);
        UUID pbb = UUID.randomUUID();

        when(hospitalizationService.getHospitalisedPatientsByHospital(
                null, pbb, null, null, null, null, null, null,PageRequest.of(0, 5))
        ).thenReturn(response);

        assertEquals(hospitalizationController.getHospitalisedPatientsByHospital(pbb, null,
                null, null,
                null, null, null, 0,
                5, null)
                        .getBody(),
                response);
    }

    @Test
    void createPatientCondition() {
        UUID lbp = UUID.randomUUID();
        PatientConditionRequest patientConditionRequest = new PatientConditionRequest();
        patientConditionRequest.setDescription("description");
        PatientConditionResponse patientConditionResponse = new PatientConditionResponse();
        patientConditionResponse.setDescription(patientConditionRequest.getDescription());

        when(hospitalizationService.createPatientCondition(lbp, patientConditionRequest))
                .thenReturn(patientConditionResponse);

        assertEquals(hospitalizationController.createPatientCondition(lbp, patientConditionRequest).getBody(),
                patientConditionResponse);

    }

    @Test
    void getPatientConditions() {
        UUID lbp = UUID.randomUUID();
        PatientConditionListResponse patientConditionListResponse = new PatientConditionListResponse(new ArrayList<>(), 0L);

        when(hospitalizationService.getPatientConditions(lbp, null, null, PageRequest.of(0, 5)))
                .thenReturn(patientConditionListResponse);

        assertEquals(hospitalizationController.getPatientConditions(lbp, null, null, 0, 5).getBody(),
                patientConditionListResponse
        );
    }

    @Test
    void createMedicalReport() {
        UUID lbp = UUID.randomUUID();
        MedicalReportRequest request = new MedicalReportRequest();
        request.setObjectiveResult("objectiveResult");
        MedicalReportResponse response = new MedicalReportResponse();
        response.setObjectiveResult(request.getObjectiveResult());

        when(hospitalizationService.createMedicalReport(lbp, request))
                .thenReturn(response);

        assertEquals(hospitalizationController.createMedicalReport(lbp, request).getBody(),
                response);
    }

    @Test
    void getMedicalReports() {
        UUID lbp = UUID.randomUUID();
        MedicalReportListResponse response = new MedicalReportListResponse(new ArrayList<>(), 0L);

        when(hospitalizationService.getMedicalReports(lbp, null, null, PageRequest.of(0, 5)))
                .thenReturn(response);

        assertEquals(hospitalizationController.getMedicalReports(lbp, null, null, 0, 5).getBody(),
                response);
    }

    @Test
    void createDischarge() {
        UUID lbp = UUID.randomUUID();
        String token = UUID.randomUUID().toString();
        DischargeRequest dischargeRequest = new DischargeRequest();
        dischargeRequest.setAnamnesis("anamnesis");
        dischargeRequest.setConclusion("conclusion");

        DischargeResponse dischargeResponse = new DischargeResponse();
        dischargeResponse.setAnamnesis(dischargeRequest.getAnamnesis());
        dischargeResponse.setConclusion(dischargeRequest.getConclusion());

        when(hospitalizationService.createDischarge(lbp, dischargeRequest, token))
                .thenReturn(dischargeResponse);

        assertEquals(hospitalizationController.createDischarge(lbp, dischargeRequest, token).getBody(),
                dischargeResponse);
    }

    @Test
    void getDischarges() {
        UUID lbp = UUID.randomUUID();
        String token = UUID.randomUUID().toString();
        DischargeListResponse response = new DischargeListResponse(new ArrayList<>(), 0L);

        when(hospitalizationService.getDischarge(lbp, null, null, PageRequest.of(0, 5), token))
                .thenReturn(response);

        assertEquals(hospitalizationController.getDischarges(lbp, null, null, 0, 5, token).getBody(),
                response);
    }
}
