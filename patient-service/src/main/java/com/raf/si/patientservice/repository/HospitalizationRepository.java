package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import com.raf.si.patientservice.model.Hospitalization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalizationRepository extends JpaRepository<Hospitalization, Long> {

    @Query(value = "select case when (count(h) > 0) then true else false end " +
            " from Hospitalization h where h.patient=:patient and h.dischargeDate is null")
    boolean patientAlreadyHospitalized(Patient patient);
}
