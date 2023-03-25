package com.raf.si.patientservice.dto.response;


import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class SchedMedExamResponse {
    private Long id;
    private UUID lbp;
    private UUID lbzDoctor;
    private Date appointmentDate;
    private String note;
    private UUID lbzNurse;
    private ExaminationStatus examinationStatus;
    private PatientArrivalStatus patientArrivalStatus;
}
