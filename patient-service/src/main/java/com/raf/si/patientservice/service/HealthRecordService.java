package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.DateBetweenRequest;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.LightHealthRecordResponse;
import com.raf.si.patientservice.dto.response.MedicalExaminationListResponse;
import com.raf.si.patientservice.dto.response.MedicalHistoryListResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HealthRecordService {

    HealthRecordResponse getHealthRecordForPatient(UUID lbp, Pageable pageable);

    LightHealthRecordResponse getLightHealthRecordForPatient(UUID lbp, Pageable pageable);

    MedicalExaminationListResponse findExaminations(UUID lbp, DateBetweenRequest request, Pageable pageable);

    MedicalHistoryListResponse findMedicalHistory(UUID lbp, String diagnosisCode, Pageable pageable);
}
