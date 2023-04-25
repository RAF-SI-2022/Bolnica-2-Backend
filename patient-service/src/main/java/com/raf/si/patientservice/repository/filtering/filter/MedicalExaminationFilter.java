package com.raf.si.patientservice.repository.filtering.filter;

import com.raf.si.patientservice.model.HealthRecord;
import lombok.*;

import java.util.Date;

@Getter
@AllArgsConstructor
public class MedicalExaminationFilter {
    private HealthRecord healthRecord;
    private Date startDate;
    private Date endDate;
    private Boolean canGetConfidential;
}
