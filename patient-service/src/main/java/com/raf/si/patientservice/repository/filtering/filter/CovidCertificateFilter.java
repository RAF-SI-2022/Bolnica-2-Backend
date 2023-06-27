package com.raf.si.patientservice.repository.filtering.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CovidCertificateFilter {

    private final UUID lbp;
    private final LocalDateTime apply;
    private final LocalDateTime end;
}
