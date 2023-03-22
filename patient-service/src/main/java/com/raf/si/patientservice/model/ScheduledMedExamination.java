package com.raf.si.patientservice.model;


import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ScheduledMedExamination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //FK
    @Column(nullable = false)
    private UUID lbp;

    //FK
    @Column(nullable = false)
    private UUID lbz_doctor;

    @Column(nullable = false)
    private Date appointmentDate;
    @Column
    private ExaminationStatus examinationStatus = ExaminationStatus.ZAKAZANO;
    @Column
    private PatientArrivalStatus patientArrivalStatus= PatientArrivalStatus.NIJE_DOSAO;
    @Column
    private String note="";

    //FK
    @Column(nullable = false)
    private UUID lbz_nurse;










}
