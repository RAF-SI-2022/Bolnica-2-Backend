package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface SchedMedExaminationService {

    SchedMedExamResponse createSchedMedExamination(SchedMedExamRequest schedMedExamRequest);
    SchedMedExamResponse updateSchedMedExaminationExamStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest);
    Page<SchedMedExamResponse> getSchedMedExaminationByLbz(UUID lbz, Optional<Date> appointmentDate, String token, int page, int size);
    SchedMedExamResponse updateSchedMedExaminationPatientArrivalStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest);
}
