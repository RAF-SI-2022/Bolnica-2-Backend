package com.raf.si.laboratoryservice.service;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.request.UpdateLabExamStatusRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;
import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface LabExamService {
    LabExamResponse createExamination(CreateLabExamRequest createLabExamRequest);

    Optional<Long> getScheduledExamCount(Timestamp date);

    List<LabExamResponse> getScheduledExams(Timestamp date, UUID lbp);

    LabExamResponse updateStatus(UpdateLabExamStatusRequest updateLabExamStatusRequest);
}
