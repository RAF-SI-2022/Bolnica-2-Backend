package com.raf.si.patientservice.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PatientConditionResponse {

    private Long id;
    private UUID lbp;
    private UUID registerLbz;
    private Date collectedInfoDate;
    private String temperature;
    private String bloodPressure;
    private String pulse;
    private String appliedTherapies;
    private String description;
}
