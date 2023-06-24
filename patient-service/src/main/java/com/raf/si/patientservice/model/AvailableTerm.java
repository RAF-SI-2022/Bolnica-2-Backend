package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.testing.Availability;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class AvailableTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateAndTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Availability availability = Availability.MOGUCE_ZAKAZATI_U_OVOM_TERMINU;

    @Column(nullable = false)
    private UUID pbo;

    @Column(nullable = false)
    private Integer availableNursesNum;

    @Column(nullable = false)
    private Integer scheduledTermsNum = 0;

    @OneToMany(mappedBy = "availableTerm", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ScheduledTesting> scheduledTestings;

    @OneToMany(mappedBy = "availableTerm", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ScheduledVaccinationCovid> scheduledVaccinationCovids;

    public void incrementAvailableNursesNum() {
        availableNursesNum++;
    }

    public void decrementAvailableNursesNum() {
        availableNursesNum--;
    }

    public void incrementScheduledTermsNum() {
        scheduledTermsNum++;
    }

    public void decrementScheduledTermsNum() {
        scheduledTermsNum--;
    }

    public void addScheduledTesting(ScheduledTesting scheduledTesting) {
        scheduledTestings.add(scheduledTesting);
    }

    public void removeScheduledTesting(ScheduledTesting scheduledTesting) {
        scheduledTestings.remove(scheduledTesting);
    }
    public void removeScheduledVaccination(ScheduledVaccinationCovid scheduledVaccinationCovid) {
        scheduledVaccinationCovids.remove(scheduledVaccinationCovid);
    }
    public void addScheduledVaccination(ScheduledVaccinationCovid scheduledVaccinationCovid){ scheduledVaccinationCovids.add(scheduledVaccinationCovid);}

}
