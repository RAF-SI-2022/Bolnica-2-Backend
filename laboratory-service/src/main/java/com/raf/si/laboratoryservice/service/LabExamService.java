package com.raf.si.laboratoryservice.service;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;

public interface LabExamService {
    LabExamResponse createExamination(CreateLabExamRequest createLabExamRequest);

}
