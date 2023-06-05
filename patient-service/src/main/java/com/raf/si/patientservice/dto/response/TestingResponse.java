package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.enums.testing.TestResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestingResponse {
    private Long id;
    private LocalDateTime dateAndTimeOfTesting;
    private TestResult testResult;
    private String reason;
    private Boolean deleted;
    private PatientConditionResponse patientCondition;
}
