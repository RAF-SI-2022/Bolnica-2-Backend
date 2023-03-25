package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.MedicalHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {

    List<MedicalHistory> findByHealthRecord(HealthRecord healthRecord, Pageable pageable);
}
