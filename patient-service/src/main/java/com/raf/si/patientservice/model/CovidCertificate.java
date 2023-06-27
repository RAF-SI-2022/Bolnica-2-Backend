package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.certificate.CovidCertificateType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class CovidCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificateNumber", nullable = false)
    private UUID certificateNumber = UUID.randomUUID();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vaccination_covid_id", referencedColumnName = "id")
    private VaccinationCovid vaccinationCovid;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "testing_id", referencedColumnName = "id")
    private Testing testing;

    @Column(name = "covid_certificate_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CovidCertificateType covidCertificateType;

    @Column(name = "date_apply", nullable = false)
    private LocalDateTime dateApply;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "date_of_issue", nullable = false)
    private LocalDateTime dateOfIssue = LocalDateTime.now();
}
