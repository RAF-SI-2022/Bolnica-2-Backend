package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.MedicalExamination;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Date;
import java.util.List;

public interface MedicalExaminationRepository extends JpaRepository<MedicalExamination, Long> {

    List<MedicalExamination> findByHealthRecord(HealthRecord healthRecord, Pageable pageable);

    List<MedicalExamination> findByHealthRecordAndDateBetween(HealthRecord healthRecord, Date startDate, Date endDate, Pageable pageable);
}
