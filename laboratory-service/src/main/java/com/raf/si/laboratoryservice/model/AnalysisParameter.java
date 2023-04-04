package com.raf.si.laboratoryservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class AnalysisParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "analysis_id", nullable = false)
    private LabAnalysis analysis;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parameter_id", nullable = false)
    private Parameter parameter;


    @OneToMany(mappedBy = "analysisParameter", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnalysisParameterResult> analysisParameterResults;
}
