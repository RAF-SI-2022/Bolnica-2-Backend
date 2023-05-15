package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Appointment;
import com.raf.si.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    @Query(value = "select case when (count(a) > 0) then true else false end " +
            "from Appointment a where a.patient=:patient and a.receiptDate between :startDate and :endDate")
    boolean patientHasAppointmentDateBetween(Patient patient, Date startDate, Date endDate);
}
