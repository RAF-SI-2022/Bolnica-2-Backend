package com.raf.si.patientservice.dto.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PatientConditionRequest {

    private Date collectedInfoDate;
    private String temperature;
    private String bloodPressure;
    private String pulse;
    private String appliedTherapies;
    private String description;

    private Boolean onRespirator;

    public boolean allNull() {
        return Stream.of(collectedInfoDate, temperature, bloodPressure, pulse, appliedTherapies, description)
                .allMatch(Objects::isNull);
    }
}
