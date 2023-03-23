package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
}
