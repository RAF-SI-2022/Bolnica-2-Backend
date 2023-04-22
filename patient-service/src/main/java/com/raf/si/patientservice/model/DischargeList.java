package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
public class DischargeList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hospitalization_id", referencedColumnName = "id")
    private Hospitalization hospitalization;
    @Column(name = "attendant_diagnoses")
    private String attendDiagnoses;
    @Column(nullable = false)
    private String anamnesis;
    @Column(name = "course_disease")
    private String courseDisease;
    @Column(nullable = false)
    private String conclusion;
    private String therapy;
    @Column(name = "prescribing_doctor", nullable = false)
    private UUID prescribingDoctor;
    @Column(name = "head_department", nullable = false)
    private UUID headDepartment;
    @Column(nullable = false)
    private Date date;
}
