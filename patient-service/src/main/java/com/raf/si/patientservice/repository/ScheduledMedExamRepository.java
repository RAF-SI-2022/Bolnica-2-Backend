package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.repository.filtering.specification.ScheduledMedExamSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduledMedExamRepository extends JpaRepository<ScheduledMedExamination,Long>, JpaSpecificationExecutor<ScheduledMedExamination> {
    Optional<Page<ScheduledMedExamination>> findByLbzDoctor(UUID lbzDoctor, Pageable pageable);
    Optional<List<ScheduledMedExamination>> findByAppointmentDateBetweenAndLbzDoctor(Date betweenAppointments, Date appointment
            , UUID lbzDoctor);
    Optional<Page<ScheduledMedExamination>> findByAppointmentDateBetweenAndLbzDoctor(Date betweenAppointments, Date appointment
            , UUID lbzDoctor, Pageable pageable);
    Optional<ScheduledMedExamination> findById(Long id);
}
