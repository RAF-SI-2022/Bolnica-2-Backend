package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {
}
