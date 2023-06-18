package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledVaccinationCovid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledVaccinationCovidRepository extends JpaRepository<ScheduledVaccinationCovid, Long>,
        JpaSpecificationExecutor<ScheduledVaccinationCovid> {

    @Query(value = "select s from ScheduledVaccinationCovid s where s.patient=:patient" +
            " and s.dateAndTime between :startDate and :endDate")
    List<ScheduledVaccinationCovid> findByPatientAndDateAndTimeBetween(Patient patient, LocalDateTime startDate, LocalDateTime endDate);
}
