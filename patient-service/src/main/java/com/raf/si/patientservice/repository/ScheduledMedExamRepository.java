package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.ScheduledMedExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduledMedExamRepository extends JpaRepository<ScheduledMedExamination,Long> {

    Optional<List<ScheduledMedExamination>> findByLbzDoctor(UUID lbzDoctor);
    Optional<List<ScheduledMedExamination>> findByAppointmentDateBetweenAndLbzDoctor(Date betweenAppointments, Date appointment
            , UUID lbzDoctor);
    Optional<ScheduledMedExamination> findById(Long id);
}
