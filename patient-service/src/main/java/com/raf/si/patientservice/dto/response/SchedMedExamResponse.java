package com.raf.si.patientservice.dto.response;


import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class SchedMedExamResponse {

    private UUID lbp;
    private UUID lbz_doctor;
    private Date appointmentDate;
    private String note;
    private UUID lbz_nurse;
    private ExaminationStatus examinationStatus;




}
