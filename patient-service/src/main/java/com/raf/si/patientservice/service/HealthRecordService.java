package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.*;
import com.raf.si.patientservice.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HealthRecordService {

    HealthRecordResponse getHealthRecordForPatient(UUID lbp, Pageable pageable);

    LightHealthRecordResponse getLightHealthRecordForPatient(UUID lbp, Pageable pageable);

    MedicalExaminationListResponse findExaminations(UUID lbp, MedicalExaminationFilterRequest request, Pageable pageable);

    MedicalHistoryListResponse findMedicalHistory(UUID lbp, String diagnosisCode, Pageable pageable);

    BasicHealthRecordResponse updateHealthRecord(UpdateHealthRecordRequest updateHealthRecordRequest, UUID lbp);

    ExtendedAllergyResponse addAllergy(AddAllergyRequest addAllergyRequest, UUID lbp);

    ExtendedVaccinationResponse addVaccination(AddVaccinationRequest addVaccinationRequest, UUID lbp);

    VaccineListResponse getAvailableVaccines();

    AllergenListResponse getAvailableAllergens();

    MessageResponse createExaminationReportRequest(UUID lbp, UUID lbz, CreateExaminationReportRequest createExaminationReportRequest);



}
