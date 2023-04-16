package com.raf.si.laboratoryservice.service;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.request.UpdateLabExamStatusRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface LabExamService {
    LabExamResponse createExamination(CreateLabExamRequest createLabExamRequest);

    Optional<Long> getScheduledExamCount(Date date);

    List<LabExamResponse> getScheduledExams(Date date, UUID lbp);

    LabExamResponse updateStatus(UpdateLabExamStatusRequest updateLabExamStatusRequest);
}
