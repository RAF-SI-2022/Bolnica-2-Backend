package com.raf.si.laboratoryservice.service.impl;

import com.raf.si.laboratoryservice.dto.request.CreateExaminationRequest;
import com.raf.si.laboratoryservice.dto.response.ExaminationResponse;
import com.raf.si.laboratoryservice.mapper.ExaminationMapper;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.repository.ScheduledLabExamRepository;
import com.raf.si.laboratoryservice.service.ExaminationService;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ExaminationServiceImpl implements ExaminationService {
    private final ScheduledLabExamRepository scheduledLabExamRepository;

    private final ExaminationMapper examinationMapper;

    public ExaminationServiceImpl(ScheduledLabExamRepository scheduledLabExamRepository, ExaminationMapper examinationMapper) {
        this.scheduledLabExamRepository = scheduledLabExamRepository;
        this.examinationMapper = examinationMapper;
    }


    @Override
    public ExaminationResponse createExamination(CreateExaminationRequest createExaminationRequest) {
        UUID lbzFromToken = TokenPayloadUtil.getTokenPayload().getLbz();
        UUID pboFromToken = TokenPayloadUtil.getTokenPayload().getPbo();
        ScheduledLabExam scheduledLabExam = scheduledLabExamRepository.save(examinationMapper.requestToModel(createExaminationRequest, lbzFromToken, pboFromToken));
        return examinationMapper.modelToResponse(scheduledLabExam);
    }
}
