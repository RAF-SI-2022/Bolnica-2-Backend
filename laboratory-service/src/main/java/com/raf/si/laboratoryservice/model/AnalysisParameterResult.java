package com.raf.si.laboratoryservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class AnalysisParameterResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lab_work_order_id", nullable = false)
    private LabWorkOrder labWorkOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "analysis_parameter_id", nullable = false)
    private AnalysisParameter analysisParameter;

    @Column
    private String result;

    @Column
    private Date dateAndTime;

    @Column
    private UUID lbzBiochemist;
}
