package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.PatientResponse;

import java.util.UUID;

public interface PatientService {

    PatientResponse createPatient(PatientRequest patientRequest);

    PatientResponse updatePatientByJmbg(PatientRequest patientRequest);

    PatientResponse updatePatientByLbp(PatientRequest patientRequest, UUID lbp);

    PatientResponse deletePatient(UUID lbp);

    PatientResponse getPatientByLbp(UUID lbp);
}
