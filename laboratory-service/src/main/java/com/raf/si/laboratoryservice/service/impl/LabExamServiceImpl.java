package com.raf.si.laboratoryservice.service.impl;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;
import com.raf.si.laboratoryservice.mapper.ExaminationMapper;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.repository.ScheduledLabExamRepository;
import com.raf.si.laboratoryservice.service.LabExamService;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class LabExamServiceImpl implements LabExamService {
    private final ScheduledLabExamRepository scheduledLabExamRepository;

    private final ExaminationMapper examinationMapper;

    public LabExamServiceImpl(ScheduledLabExamRepository scheduledLabExamRepository, ExaminationMapper examinationMapper) {
        this.scheduledLabExamRepository = scheduledLabExamRepository;
        this.examinationMapper = examinationMapper;
    }


    @Override
    public LabExamResponse createExamination(CreateLabExamRequest createLabExamRequest) {
        UUID lbzFromToken = TokenPayloadUtil.getTokenPayload().getLbz();
        UUID pboFromToken = TokenPayloadUtil.getTokenPayload().getPbo();
        ScheduledLabExam scheduledLabExam = scheduledLabExamRepository.save(examinationMapper.requestToModel(createLabExamRequest, lbzFromToken, pboFromToken));
        return examinationMapper.modelToResponse(scheduledLabExam);
    }


}
