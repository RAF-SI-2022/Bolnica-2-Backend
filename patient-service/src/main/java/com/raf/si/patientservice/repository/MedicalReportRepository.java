package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.MedicalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long>,
        JpaSpecificationExecutor<MedicalReport> {
}
