package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.testing.TestResult;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Testing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date date = new Date();

    @Column
    private Boolean deleted = false;

    @Column(nullable = false)
    private TestResult testResult = TestResult.NEOBRADJEN;

    @Column(nullable = false)
    private UUID nurseLbz;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;

    @OneToOne(mappedBy = "testing")
    private PatientCondition patientCondition;
}
