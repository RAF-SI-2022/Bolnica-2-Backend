package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.MedicalExamination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Date;
import java.util.List;

public interface MedicalExaminationRepository extends JpaRepository<MedicalExamination, Long> {

    Page<MedicalExamination> findByHealthRecord(HealthRecord healthRecord, Pageable pageable);

    Page<MedicalExamination> findByHealthRecordAndConfidential(HealthRecord healthRecord, Boolean confidential, Pageable pageable);

    Page<MedicalExamination> findByHealthRecordAndDateBetween(HealthRecord healthRecord, Date startDate, Date endDate, Pageable pageable);

    Page<MedicalExamination> findByHealthRecordAndConfidentialAndDateBetween(HealthRecord healthRecord, Boolean confidential, Date startDate, Date endDate, Pageable pageable);
}
