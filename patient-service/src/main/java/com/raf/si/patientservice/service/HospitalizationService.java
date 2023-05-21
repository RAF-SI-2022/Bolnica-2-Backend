package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.response.HospitalisedPatientsListResponse;
import com.raf.si.patientservice.dto.response.HospitalizationResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HospitalizationService {
    HospitalizationResponse hospitalize(HospitalizationRequest request, String token);

    HospitalisedPatientsListResponse getHospitalisedPatients(String token, UUID pbo, UUID lbp, String firstName, String lastName, String jmbg, Pageable pageable);
}
