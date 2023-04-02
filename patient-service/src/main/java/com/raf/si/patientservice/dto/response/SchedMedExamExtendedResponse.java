package com.raf.si.patientservice.dto.response;


import lombok.Data;

@Data
public class SchedMedExamExtendedResponse extends SchedMedExamResponse{
    private PatientResponse patientResponse;
}
