package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.AnalysisParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisParameterRepository extends JpaRepository<AnalysisParameter, Long> {
}
