package com.raf.si.patientservice.repository.filtering.filter;

import com.raf.si.patientservice.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class CovidSchedMedExamFilter {
    private Patient patient;
    private Date date;
}
