package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledTesting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledTestingRepository extends JpaRepository<ScheduledTesting, Long> {

    @Query(value = "select s from ScheduledTesting s where s.patient=:patient" +
            " and s.dateAndTime between :startDate and :endDate")
    List<ScheduledTesting> findByPatientAndDateAndTimeBetween(Patient patient, Date startDate, Date endDate);
}
