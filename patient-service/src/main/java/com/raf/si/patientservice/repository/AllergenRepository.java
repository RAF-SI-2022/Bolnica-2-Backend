package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergenRepository extends JpaRepository<Allergen, Long> {
}
