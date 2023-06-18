package com.raf.si.patientservice.repository;


import com.raf.si.patientservice.model.VaccinationCovid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccinationCovidRepository extends JpaRepository<VaccinationCovid, Long> {
}
