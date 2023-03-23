package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.AddAllergyRequest;
import com.raf.si.patientservice.dto.request.AddVaccinationRequest;
import com.raf.si.patientservice.dto.request.UpdateHealthRecordRequest;
import com.raf.si.patientservice.dto.response.MessageResponse;
import com.raf.si.patientservice.service.HealthrecordService;

import java.util.UUID;

public class HealthrecordServiceImpl implements HealthrecordService {


    @Override
    public MessageResponse updateHealthrecord(UpdateHealthRecordRequest updateHealthRecordRequest, UUID lbp) {
        return null;
    }

    @Override
    public MessageResponse addAllergy(AddAllergyRequest addAllergyRequest, UUID lbp) {
        return null;
    }

    @Override
    public MessageResponse addVaccination(AddVaccinationRequest addVaccinationRequest, UUID lbp) {
        return null;
    }
}
