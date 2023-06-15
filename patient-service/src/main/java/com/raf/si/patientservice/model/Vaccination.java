package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Vaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "health_record_id", nullable = false)
    private HealthRecord healthRecord;

    @Column
    private Date vaccinationDate = new Date(System.currentTimeMillis());

    @OneToOne(mappedBy = "vaccination", fetch = FetchType.EAGER)
    private ScheduledVaccination scheduledVaccination;
    
    @Column
    private Boolean deleted = false;
}
