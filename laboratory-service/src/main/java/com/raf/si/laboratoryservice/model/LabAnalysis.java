package com.raf.si.laboratoryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class LabAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String abbreviation;

    @OneToMany(mappedBy = "analysis", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnalysisParameter> analysisParameters;
}
