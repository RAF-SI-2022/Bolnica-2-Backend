package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.CovidCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CovidCertificateRepository extends JpaRepository<CovidCertificate, Long>,
        JpaSpecificationExecutor<CovidCertificate> {
}
