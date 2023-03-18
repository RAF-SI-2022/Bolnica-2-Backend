package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.medicalhistory.TreatmentResult;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Getter
@Setter
public class MedicalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Boolean confidential = false;

    @Column(nullable = false)
    private Date illnessStart;

    @Column
    private Date illnessEnd;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TreatmentResult treatmentResult;

    @Column(nullable = false)
    private String currentStateDescription;

    @Column(nullable = false)
    private Date validFrom;

    @Column(nullable = false)
    private Date validUntil = Date.valueOf("9999-12-31");

    @Column
    private Boolean valid;

    @ManyToOne
    private Diagnosis diagnosis;

    @ManyToOne
    @JoinColumn(name = "health_record_id", nullable = false)
    private HealthRecord healthRecord;
}
