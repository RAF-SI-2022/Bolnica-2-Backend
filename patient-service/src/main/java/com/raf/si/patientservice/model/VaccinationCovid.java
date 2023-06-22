package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
public class VaccinationCovid {

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
    private LocalDateTime dateTime;

    @OneToOne(mappedBy = "vaccination", fetch = FetchType.EAGER)
    private ScheduledVaccinationCovid scheduledVaccinationCovid;

    @Column
    private String doseReceived = "0";

    @Column(nullable = false)
    private UUID performerLbz;

    @Column
    private Boolean deleted = false;

    public void incrementDosage(String dosage){
        int doseInc= Integer.parseInt(dosage) + 1;
        doseReceived = String.valueOf(doseInc);
    }

    public Long getDosageAsLong(){
        return Long.parseLong(doseReceived);
    }

    public void decrementDosage(){
        int doseInc= Integer.parseInt(doseReceived) - 1;
        doseReceived = doseInc < 0 ? "0" : String.valueOf(doseInc);
    }
}
