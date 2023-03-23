package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.AddAllergyRequest;
import com.raf.si.patientservice.dto.request.AddVaccinationRequest;
import com.raf.si.patientservice.dto.request.UpdateHealthRecordRequest;
import com.raf.si.patientservice.dto.response.MessageResponse;

import java.util.UUID;

public interface HealthrecordService {

    MessageResponse updateHealthrecord(UpdateHealthRecordRequest updateHealthRecordRequest, UUID lbp);

    MessageResponse addAllergy(AddAllergyRequest addAllergyRequest, UUID lbp);

    MessageResponse addVaccination(AddVaccinationRequest addVaccinationRequest, UUID lbp);

}
