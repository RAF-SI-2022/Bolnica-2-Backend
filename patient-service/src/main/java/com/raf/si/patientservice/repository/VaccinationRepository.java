package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Vaccination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VaccinationRepository  extends JpaRepository<Vaccination, Long> {

    Page<Vaccination> findByHealthRecord(HealthRecord healthRecord, Pageable pageable);
}
