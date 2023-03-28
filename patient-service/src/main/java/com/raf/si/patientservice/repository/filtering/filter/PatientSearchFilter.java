package com.raf.si.patientservice.repository.filtering.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientSearchFilter {
    private UUID lbp;
    private String firstName;
    private String lastName;
    private String jmbg;
    private Boolean includeDeleted;
}
