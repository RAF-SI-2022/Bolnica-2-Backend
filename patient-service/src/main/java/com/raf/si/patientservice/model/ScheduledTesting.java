package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ScheduledTesting {

    private static final int testDurationMinutes = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateAndTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExaminationStatus testStatus = ExaminationStatus.ZAKAZANO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PatientArrivalStatus patientArrivalStatus = PatientArrivalStatus.NIJE_DOSAO;

    @Column
    private String note;

    @Column(nullable = false)
    private UUID schedulerLbz;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "available_term_id", referencedColumnName = "id", nullable = false)
    private AvailableTerm availableTerm;

    public static int getTestDurationMinutes() {
        return testDurationMinutes;
    }
}
