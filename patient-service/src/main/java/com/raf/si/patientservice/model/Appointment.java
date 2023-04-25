package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.appointment.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "department_id", nullable = false)
    private Long departmentId;
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;
    @Column(name = "receipt_date", nullable = false)
    private Date receiptDate;
    private AppointmentStatus status = AppointmentStatus.ZAKAZAN;
    private String note;
    @Column(name = "employee_lbz", nullable = false)
    private UUID employeeLBZ;
}
