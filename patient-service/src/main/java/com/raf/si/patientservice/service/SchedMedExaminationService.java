package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SchedMedExaminationService {

    SchedMedExamResponse createSchedMedExamination(SchedMedExamRequest schedMedExamRequest);
    SchedMedExamResponse updateSchedMedExaminationExamStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest);
    List<SchedMedExamResponse> getSchedMedExaminationByLbz(UUID lbz, Date appointmentDate, String token);
    SchedMedExamResponse updateSchedMedExaminationPatientArrivalStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest);
}
