package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Allergy;
import com.raf.si.patientservice.model.HealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {

    Page<Allergy> findByHealthRecord(HealthRecord healthRecord, Pageable pageable);
}
