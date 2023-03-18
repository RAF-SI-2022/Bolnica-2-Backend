package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Allergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allergen_id", nullable = false)
    private Allergen allergen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "health_record_id", nullable = false)
    private HealthRecord healthRecord;

    @Column
    private Boolean deleted = false;
}
