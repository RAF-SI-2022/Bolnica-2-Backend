package com.raf.si.patientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.raf.si.patientservice.model.Hospitalization;


public interface HospitalizationRepository extends JpaRepository<Hospitalization, Long> {
}
