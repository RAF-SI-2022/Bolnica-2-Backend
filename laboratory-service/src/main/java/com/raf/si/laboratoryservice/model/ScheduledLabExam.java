package com.raf.si.laboratoryservice.model;

import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ScheduledLabExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date scheduledDate;

    @Column
    @Enumerated(EnumType.STRING)
    private ExamStatus examStatus = ExamStatus.ZAKAZANO;

    @Column
    private String note;

    //FK
    @Column(nullable = false)
    private UUID pbo;

    @Column(nullable = false)
    private UUID lbp;

    @Column(nullable = false)
    private UUID lbz;
}
