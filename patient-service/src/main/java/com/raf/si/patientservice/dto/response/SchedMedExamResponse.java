package com.raf.si.patientservice.dto.response;


import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class SchedMedExamResponse {
    protected Long id;
    protected UUID lbp;
    protected UUID lbzDoctor;
    protected Date appointmentDate;
    protected String note;
    protected UUID lbzNurse;
    protected ExaminationStatus examinationStatus;
    protected PatientArrivalStatus patientArrivalStatus;
}
