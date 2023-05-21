package com.raf.si.patientservice.repository.filtering.filter;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class HospitalisedPatientSearchFilter {

    private final UUID lbp;
    private final UUID pbo;
    private final String firstName;
    private final String lastName;
    private final String jmbg;
}
