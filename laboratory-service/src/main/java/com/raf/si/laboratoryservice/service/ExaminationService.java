package com.raf.si.laboratoryservice.service;

import com.raf.si.laboratoryservice.dto.request.CreateExaminationRequest;
import com.raf.si.laboratoryservice.dto.response.ExaminationResponse;

public interface ExaminationService {
    ExaminationResponse createExamination(CreateExaminationRequest createExaminationRequest);

}
