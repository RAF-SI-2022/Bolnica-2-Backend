package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.LabAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabAnalysisRepository extends JpaRepository<LabAnalysis, Long> {
}
