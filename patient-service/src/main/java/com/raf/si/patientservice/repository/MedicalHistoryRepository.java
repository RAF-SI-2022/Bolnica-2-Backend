package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
}
