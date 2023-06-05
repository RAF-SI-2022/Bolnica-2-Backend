package com.raf.si.patientservice.repository.filtering.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class PatientConditionFilter {

    private final UUID lbp;
    private final Date dateFrom;
    private final Date dateTo;
}
