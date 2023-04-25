package com.raf.si.laboratoryservice.model;

import com.raf.si.laboratoryservice.model.enums.parameter.ParameterType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "parameter")
@Entity
@Getter
@Setter
public class Parameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParameterType type;

    @Column
    private String measureUnit;

    @Column
    private Double lowerBound;

    @Column
    private Double upperBound;
}
