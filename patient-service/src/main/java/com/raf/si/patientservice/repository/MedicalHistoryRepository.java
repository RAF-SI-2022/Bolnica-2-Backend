package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.MedicalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {

    Page<MedicalHistory> findByHealthRecord(HealthRecord healthRecord, Pageable pageable);

    Page<MedicalHistory> findByHealthRecordAndConfidential(HealthRecord healthRecord, Boolean confidential, Pageable pageable);

    Page<MedicalHistory> findByHealthRecordAndDiagnosis_code(HealthRecord healthRecord, String diagnosisCode, Pageable pageable);

    Page<MedicalHistory> findByHealthRecordAndConfidentialAndDiagnosis_code(HealthRecord healthRecord, Boolean confidential, String diagnosisCode, Pageable pageable);
}
