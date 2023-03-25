package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.LightHealthRecordResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HealthRecordService {

    HealthRecordResponse getHealthRecordForPatient(UUID lbp, Pageable pageable);

    LightHealthRecordResponse getLightHealthRecordForPatient(UUID lbp, Pageable pageable);
}
