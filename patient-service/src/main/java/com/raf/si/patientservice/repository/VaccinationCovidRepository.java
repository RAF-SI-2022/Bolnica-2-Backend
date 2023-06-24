package com.raf.si.patientservice.repository;


import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.VaccinationCovid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaccinationCovidRepository extends JpaRepository<VaccinationCovid, Long> {

    List<VaccinationCovid> findByHealthRecord_Patient(Patient patient);
}
