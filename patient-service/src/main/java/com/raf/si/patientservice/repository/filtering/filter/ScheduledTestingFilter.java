package com.raf.si.patientservice.repository.filtering.filter;

import com.raf.si.patientservice.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduledTestingFilter {
    private Patient patient;
    private LocalDateTime date;
}
