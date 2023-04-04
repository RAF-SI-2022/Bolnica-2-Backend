package com.raf.si.patientservice.repository.filtering.filter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ScheduledMedExamFilter {
    private UUID lbz;
    private Date appointmentDate;
}
