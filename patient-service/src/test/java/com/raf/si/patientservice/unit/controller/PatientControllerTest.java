package com.raf.si.patientservice.unit.controller;


import com.raf.si.patientservice.controller.PatientController;
import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.PatientListResponse;
import com.raf.si.patientservice.dto.response.PatientResponse;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.utils.TokenPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @Test
    void createPatientTest_Success() throws ParseException {
        PatientRequest request = makePatientRequest();
        PatientResponse response = new PatientResponse();

        when(patientService.createPatient(request)).thenReturn(response);

        assertEquals(patientController.createPatient(request),
                ResponseEntity.ok(response));
    }

    @Test
    void updatePatientByJmbgTest_Success() throws ParseException {
        PatientRequest request = makePatientRequest();
        PatientResponse response = new PatientResponse();

        when(patientService.updatePatientByJmbg(request)).thenReturn(response);

        assertEquals(patientController.updatePatientByJmbg(request),
                ResponseEntity.ok(response));
    }

    @Test
    void updatePatientByLbpTest_Success() throws ParseException {
        PatientRequest request = makePatientRequest();
        PatientResponse response = new PatientResponse();
        UUID lbp = UUID.randomUUID();

        when(patientService.updatePatientByLbp(request, lbp)).thenReturn(response);

        assertEquals(patientController.updatePatientByLbp(request, lbp),
                ResponseEntity.ok(response));
    }

    @Test
    void deletePatientTest_Success(){
        UUID lbp = UUID.randomUUID();
        PatientResponse response = new PatientResponse();

        when(patientService.deletePatient(lbp)).thenReturn(response);

        assertEquals(patientController.deletePatient(lbp),
                ResponseEntity.ok(response));
    }

    @Test
    void getPatientByLbpTest_Success(){
        UUID lbp = UUID.randomUUID();
        PatientResponse response = new PatientResponse();

        when(patientService.getPatientByLbp(lbp)).thenReturn(response);

        assertEquals(patientController.getPatientByLbp(lbp),
                ResponseEntity.ok(response));
    }

    @Test
    void getPatientsTest_Success(){
        PatientListResponse response = new PatientListResponse();

        when(patientService.getPatients(any(), any(), any(), any(), any(), any()))
                .thenReturn(response);

        assertEquals(patientController.getPatients(null, null, null, null, null, 0, 5),
                ResponseEntity.ok(response));
    }




    private PatientRequest makePatientRequest() throws ParseException {
        PatientRequest patientRequest = new PatientRequest();

        patientRequest.setJmbg("1342002345612");
        patientRequest.setFirstName("Pacijent");
        patientRequest.setLastName("Pacijentovic");
        patientRequest.setParentName("Roditelj");
        patientRequest.setGender("Muški");
        patientRequest.setBirthDate(new Date());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        patientRequest.setDeathDate(formatter.parse("12-12-2200"));
        patientRequest.setBirthplace("Resnjak");
        patientRequest.setCitizenshipCountry("SRB");
        patientRequest.setCountryOfLiving("AFG");

        patientRequest.setAddress("Jurija Gagarina 16");
        patientRequest.setPlaceOfLiving("Novi Beograd");
        patientRequest.setPhoneNumber("0601234567");
        patientRequest.setEmail("pacijent.pacijentovic@gmail.com");
        patientRequest.setCustodianJmbg("0101987123456");
        patientRequest.setCustodianName("Staratelj Starateljovic");
        patientRequest.setProfession("Programer");
        patientRequest.setChildrenNum(2);
        patientRequest.setEducation("Osnovno obrazovanje");
        patientRequest.setMaritalStatus("Razveden");
        patientRequest.setFamilyStatus("Usvojen");

        return patientRequest;
    }

    private TokenPayload makeTokenPayload(){
        TokenPayload payload = new TokenPayload();
        payload.setPermissions(new ArrayList<>());
        return payload;
    }
}
