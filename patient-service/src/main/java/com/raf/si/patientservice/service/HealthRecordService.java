package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.response.HealthRecordResponse;

import java.util.UUID;

public interface HealthRecordService {

    HealthRecordResponse getHealthRecordForPatient(UUID lbp);
}
