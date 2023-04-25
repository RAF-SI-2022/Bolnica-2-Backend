package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.LabAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabAnalysisRepository extends JpaRepository<LabAnalysis, Long> {
    @Query(value = "select l from LabAnalysis l where l.name in (:names)")
    Optional<List<LabAnalysis>> findByNames(List<String> names);
}
