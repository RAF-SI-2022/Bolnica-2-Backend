package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.PatientResponse;

public interface PatientService {

    PatientResponse createPatient(PatientRequest patientRequest);

    PatientResponse updatePatient(PatientRequest patientRequest);

    PatientResponse deletePatient(Long id);
}
