package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.testing.TestResult;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Testing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateAndTime = LocalDateTime.now();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TestResult testResult = TestResult.NEOBRADJEN;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private Boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_condition_id", referencedColumnName = "id")
    private PatientCondition patientCondition;

    @OneToOne(mappedBy = "testing", fetch = FetchType.EAGER)
    private ScheduledTesting scheduledTesting;
}
