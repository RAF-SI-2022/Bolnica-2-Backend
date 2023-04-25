package com.raf.si.patientservice.repository.filtering.filter;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PatientSearchFilter {
    private UUID lbp;
    private String firstName;
    private String lastName;
    private String jmbg;
    private Boolean includeDeleted;
}
