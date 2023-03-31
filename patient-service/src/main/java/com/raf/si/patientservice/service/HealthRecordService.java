package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.AddAllergyRequest;
import com.raf.si.patientservice.dto.request.AddVaccinationRequest;
import com.raf.si.patientservice.dto.request.MedicalExaminationFilterRequest;
import com.raf.si.patientservice.dto.request.UpdateHealthRecordRequest;
import com.raf.si.patientservice.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HealthRecordService {

    HealthRecordResponse getHealthRecordForPatient(UUID lbp, Pageable pageable);

    LightHealthRecordResponse getLightHealthRecordForPatient(UUID lbp, Pageable pageable);

    MedicalExaminationListResponse findExaminations(UUID lbp, MedicalExaminationFilterRequest request, Pageable pageable);

    MedicalHistoryListResponse findMedicalHistory(UUID lbp, String diagnosisCode, Pageable pageable);

    MessageResponse updateHealthrecord(UpdateHealthRecordRequest updateHealthRecordRequest, UUID lbp);

    MessageResponse addAllergy(AddAllergyRequest addAllergyRequest, UUID lbp);

    MessageResponse addVaccination(AddVaccinationRequest addVaccinationRequest, UUID lbp);

    VaccineListResponse getAvailableVaccines();

    AllergenListResponse getAvailableAllergens();



}
