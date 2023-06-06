package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("select v from Visit v where v.patient.lbp = :lbp")
    Page<Visit> getAllVisitsByPatientLbp(@PathVariable(name = "lbp") UUID lbp, Pageable pageable);
}
