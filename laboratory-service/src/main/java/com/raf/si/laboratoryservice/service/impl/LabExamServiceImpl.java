package com.raf.si.laboratoryservice.service.impl;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamListResponse;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;
import com.raf.si.laboratoryservice.mapper.LabExamMapper;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.repository.ScheduledLabExamRepository;
import com.raf.si.laboratoryservice.service.LabExamService;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class LabExamServiceImpl implements LabExamService {
    private final ScheduledLabExamRepository scheduledLabExamRepository;

    private final LabExamMapper labExamMapper;

    public LabExamServiceImpl(ScheduledLabExamRepository scheduledLabExamRepository, LabExamMapper labExamMapper) {
        this.scheduledLabExamRepository = scheduledLabExamRepository;
        this.labExamMapper = labExamMapper;
    }


    @Override
    public LabExamResponse createExamination(CreateLabExamRequest createLabExamRequest) {
        UUID lbzFromToken = TokenPayloadUtil.getTokenPayload().getLbz();
        UUID pboFromToken = TokenPayloadUtil.getTokenPayload().getPbo();
        ScheduledLabExam scheduledLabExam = scheduledLabExamRepository.save(labExamMapper.requestToModel(createLabExamRequest, lbzFromToken, pboFromToken));
        return labExamMapper.modelToResponse(scheduledLabExam);
    }

    @Override
    public Optional<Long> getScheduledExamCount(Timestamp date) {
        LocalDateTime localDateTime = date.toLocalDateTime().plusDays(1).withHour(0).withMinute(0).withSecond(0);
        Timestamp endDate = Timestamp.valueOf(localDateTime);
        UUID pbo = TokenPayloadUtil.getTokenPayload().getPbo();
        return Optional.of(scheduledLabExamRepository.countByPboIdAndDateRange(pbo, date, endDate));
    }
}
