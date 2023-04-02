package com.raf.si.patientservice.repository.filtering.filter;

import com.raf.si.patientservice.model.HealthRecord;
import lombok.*;

@Getter
@AllArgsConstructor
public class MedicalHistoryFilter {
    private HealthRecord healthRecord;
    private String diagnosisCode;
    private Boolean canGetConfidential;
}
