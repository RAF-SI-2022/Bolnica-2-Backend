package com.raf.si.patientservice.repository.filtering.filter;

import com.raf.si.patientservice.model.HealthRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalExaminationFilter {
    private HealthRecord healthRecord;
    private Date startDate;
    private Date endDate;
    private Boolean canGetConfidential;
}
