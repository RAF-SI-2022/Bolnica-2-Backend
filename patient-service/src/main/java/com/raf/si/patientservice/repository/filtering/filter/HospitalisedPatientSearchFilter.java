package com.raf.si.patientservice.repository.filtering.filter;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
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
    private final String diagnosis;
    private Boolean onRespirator;
    private Boolean isImmunized;
    private final List<UUID> departmentIds;
}
