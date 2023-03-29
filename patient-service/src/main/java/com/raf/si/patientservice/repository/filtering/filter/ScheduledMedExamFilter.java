package com.raf.si.patientservice.repository.filtering.filter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledMedExamFilter {
    private UUID lbz;
    private Date appointmentDate;
}
