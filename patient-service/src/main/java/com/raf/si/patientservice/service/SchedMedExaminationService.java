package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;

public interface SchedMedExaminationService {

    SchedMedExamResponse createSchedMedExamination(SchedMedExamRequest schedMedExamRequest);
    SchedMedExamResponse updateSchedMedExaminationExamStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest);
    String deleteSchedMedExamination(Long id);
    SchedMedExamResponse updateSchedMedExaminationPatientArrivalStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest);
}
