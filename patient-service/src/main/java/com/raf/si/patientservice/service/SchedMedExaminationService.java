package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.TimeRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateTermsNewShiftRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamListResponse;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SchedMedExaminationService {

    SchedMedExamResponse createSchedMedExamination(SchedMedExamRequest schedMedExamRequest, String token);
    SchedMedExamResponse createSchedMedExaminationLocked(SchedMedExamRequest schedMedExamRequest, String token);
    SchedMedExamResponse updateSchedMedExaminationExamStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest);
    SchedMedExamResponse deleteSchedMedExamination(Long id);
    SchedMedExamListResponse getSchedMedExaminationByLbz(UUID lbz, Date appointmentDate, String token, Pageable pageable);
    SchedMedExamResponse updateSchedMedExaminationPatientArrivalStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest);
    ScheduledMedExamination findSchedMedExamById(Long id);
    List<Date> doctorHasScheduledExamsForTimeSlot(UUID lbz, UpdateTermsNewShiftRequest request);
}
