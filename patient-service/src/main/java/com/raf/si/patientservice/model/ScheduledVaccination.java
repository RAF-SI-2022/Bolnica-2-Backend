package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class ScheduledVaccination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;

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
    @JoinColumn(name = "available_term_id", referencedColumnName = "id", nullable = false)
    private AvailableTerm availableTerm;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "vaccination_id", referencedColumnName = "id")
    private Vaccination vaccination;
}
