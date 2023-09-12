package com.raf.si.patientservice.repository.filtering.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class MedicalReportFilter {

    private final UUID lbp;
    private final Date from;
    private final Date to;
    private final boolean confidential;

    private final String diagnosis;
}
