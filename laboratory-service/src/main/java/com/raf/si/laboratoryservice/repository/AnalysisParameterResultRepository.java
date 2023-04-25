package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.AnalysisParameter;
import com.raf.si.laboratoryservice.model.AnalysisParameterResult;
import com.raf.si.laboratoryservice.model.LabWorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisParameterResultRepository extends JpaRepository<AnalysisParameterResult, Long> {
    Optional<AnalysisParameterResult> findAnalysisParameterResultByLabWorkOrderAndAnalysisParameter(
            LabWorkOrder labWorkOrder, AnalysisParameter analysisParameter);

}
