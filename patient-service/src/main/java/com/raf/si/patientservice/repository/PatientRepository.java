package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    Optional<Patient> findByJmbg(String jmbg);

    Optional<Patient> findByJmbgAndDeleted(String jmbg, Boolean deleted);

    Optional<Patient> findByLbp(UUID lbp);

    Optional<Patient> findByLbpAndDeleted(UUID lbp, Boolean deleted);
}
