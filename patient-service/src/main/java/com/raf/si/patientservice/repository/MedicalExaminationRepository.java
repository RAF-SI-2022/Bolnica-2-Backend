package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.MedicalExamination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalExaminationRepository extends JpaRepository<MedicalExamination, Long> {
}
