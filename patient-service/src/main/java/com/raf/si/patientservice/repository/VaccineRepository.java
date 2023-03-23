package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
}
