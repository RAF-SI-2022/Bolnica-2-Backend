package com.raf.si.patientservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raf.si.patientservice.model.enums.medicalhistory.TreatmentResult;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private Date validUntil;

    @Column
    private Boolean valid;

    @JsonIgnore
    @Column
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.EAGER)
    private Diagnosis diagnosis;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "health_record_id", nullable = false)
    private HealthRecord healthRecord;

    @SneakyThrows
    public MedicalHistory(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        validUntil = formatter.parse("31-Dec-9999");
    }
}
