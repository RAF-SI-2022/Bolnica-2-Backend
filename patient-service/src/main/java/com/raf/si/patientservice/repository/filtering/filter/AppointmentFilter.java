package com.raf.si.patientservice.repository.filtering.filter;

import com.raf.si.patientservice.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AppointmentFilter {
    private Patient patient;
    private Date date;
    private UUID pbo;
}
