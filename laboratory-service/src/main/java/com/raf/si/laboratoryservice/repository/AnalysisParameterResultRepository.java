package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.AnalysisParameterResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisParameterResultRepository extends JpaRepository<AnalysisParameterResult, Long> {
}
