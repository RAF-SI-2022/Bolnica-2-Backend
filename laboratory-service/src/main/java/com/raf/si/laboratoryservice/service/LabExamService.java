package com.raf.si.laboratoryservice.service;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;


public interface LabExamService {
    LabExamResponse createExamination(CreateLabExamRequest createLabExamRequest);

    Optional<Long> getScheduledExamCount(Timestamp date);

}
