package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
import com.raf.si.patientservice.model.enums.testing.Availability;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ScheduledTesting {

    private static final long testDurationMillis = 20 * 60 * 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date dateAndTime;

    @Column
    private ExaminationStatus testStatus = ExaminationStatus.ZAKAZANO;

    @Column
    private PatientArrivalStatus patientArrivalStatus = PatientArrivalStatus.NIJE_DOSAO;

    @Column
    private String note;

    @Column(nullable = false)
    private Availability availability;

    @Column(nullable = false)
    private UUID schedulerLbz;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;
}
