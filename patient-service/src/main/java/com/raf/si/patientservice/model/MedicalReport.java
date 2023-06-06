package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
public class MedicalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;
    @Column(name = "doctor_lbz", nullable = false)
    private UUID doctorLBZ;
    @Column(nullable = false)
    private Date date = new Date();
    @Column(name = "confidentiality_indicator", nullable = false)
    private Boolean confidentIndicator = false;
    @Column(name = "objective_result", nullable = false)
    private String objectiveResult;
    private String diagnosis;
    @Column(name = "proposed_therapy")
    private String proposedTherapy;
    private String advice;
    @Column(nullable = false)
    private Boolean isDeleted = false;

}
