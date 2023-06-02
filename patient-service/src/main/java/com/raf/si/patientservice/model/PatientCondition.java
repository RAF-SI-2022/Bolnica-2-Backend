package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
public class PatientCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;
    @Column(name = "register_lbz", nullable = false)
    private UUID registerLbz;
    @Column(name = "collected_info_date", nullable = false)
    private Date collectedInfoDate;
    private String temperature;
    @Column(name = "blood_pressure")
    private String bloodPressure;
    private String pulse;
    @Column(name = "applied_therapies")
    private String appliedTherapies;
    private String description;
    private Boolean onRespirator = false;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "testing_id", referencedColumnName = "id")
    private Testing testing;
}
