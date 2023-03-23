package com.raf.si.patientservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class MedicalExamination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date date;

    @Column
    private Boolean confidential = false;

    @Column
    private String mainSymptoms;

    @Column
    private String currentIllness;

    @Column
    private String anamnesis;

    @Column
    private String familyAnamnesis;

    @Column
    private String patientOpinion;

    @Column(nullable = false)
    private String objectiveFinding;

    @Column
    private String suggestedTherapy;

    @Column
    private String advice;

    @JsonIgnore
    @Column
    private Boolean deleted = false;

    //FK
    @Column(nullable = false)
    private UUID lbz;

    @ManyToOne
    private Diagnosis diagnosis;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "health_record_id", nullable = false)
    private HealthRecord healthRecord;
}
