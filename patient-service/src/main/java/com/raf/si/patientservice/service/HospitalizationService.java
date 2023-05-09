package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.response.HospitalizationResponse;

public interface HospitalizationService {
    HospitalizationResponse hospitalize(HospitalizationRequest request, String token);
}
