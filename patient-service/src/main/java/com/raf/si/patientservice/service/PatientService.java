package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.HospitalizationResponse;
import com.raf.si.patientservice.dto.response.PatientListResponse;
import com.raf.si.patientservice.dto.response.PatientResponse;
import com.raf.si.patientservice.model.Patient;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PatientService {

    PatientResponse createPatient(PatientRequest patientRequest);

    PatientResponse updatePatientByJmbg(PatientRequest patientRequest);

    PatientResponse updatePatientByLbp(PatientRequest patientRequest, UUID lbp);

    PatientResponse deletePatient(UUID lbp);

    PatientResponse getPatientByLbp(UUID lbp);

    PatientListResponse getPatients(UUID lbp, String firstName, String lastName, String jmbg, Boolean includeDeleted, Pageable pageable);



    Patient findPatient(UUID lbp);

    Patient findPatient(String jmbg);
}
