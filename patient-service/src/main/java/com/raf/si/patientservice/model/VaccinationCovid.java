package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class VaccinationCovid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vaccine_id", referencedColumnName = "id", nullable = false)
    private Vaccine vaccine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "health_record_id", referencedColumnName = "id", nullable = false)
    private HealthRecord healthRecord;

    @Column
    private Date vaccinationDate = new Date(System.currentTimeMillis());

    @OneToOne(mappedBy = "vaccination", fetch = FetchType.EAGER)
    private ScheduledVaccinationCovid scheduledVaccinationCovid;

    @Column
    private Long doseReceived = Long.valueOf(0);

    @Column
    private Boolean deleted = false;

    public void incrementDosage(){
        doseReceived = doseReceived + 1;
    }

    public void decrementDosage(){
        long doseInc= doseReceived - 1;
        doseReceived = doseInc < 0 ? Long.valueOf(0) : Long.valueOf(doseInc);
    }
}
