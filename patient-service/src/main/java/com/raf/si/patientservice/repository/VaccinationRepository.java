package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccinationRepository  extends JpaRepository<Vaccination, Long> {
}
