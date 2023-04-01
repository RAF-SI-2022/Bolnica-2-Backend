package com.raf.si.patientservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SchedMedExamExtendedResponse extends SchedMedExamResponse{
    private PatientResponse patientResponse;
}
