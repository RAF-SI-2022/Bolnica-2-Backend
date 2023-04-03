package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    Optional<HealthRecord> findById(Long id);

}
