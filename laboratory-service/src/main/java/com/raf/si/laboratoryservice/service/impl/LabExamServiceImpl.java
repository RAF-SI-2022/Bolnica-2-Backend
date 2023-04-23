package com.raf.si.laboratoryservice.service.impl;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.request.UpdateLabExamStatusRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;
import com.raf.si.laboratoryservice.exception.BadRequestException;
import com.raf.si.laboratoryservice.exception.NotFoundException;
import com.raf.si.laboratoryservice.mapper.LabExamMapper;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;
import com.raf.si.laboratoryservice.repository.ScheduledLabExamRepository;
import com.raf.si.laboratoryservice.repository.filtering.filter.LabExamFilter;
import com.raf.si.laboratoryservice.repository.filtering.specification.LabExamSpecification;
import com.raf.si.laboratoryservice.service.LabExamService;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    public Optional<Long> getScheduledExamCount(Date date) {
        Date endDate = DateUtils.addDays(date, 1);
        UUID pbo = TokenPayloadUtil.getTokenPayload().getPbo();
        return Optional.of(scheduledLabExamRepository.countByPboIdAndDateRange(pbo, date, endDate));
    }

    @Override
    public List<LabExamResponse> getScheduledExams(Date date, UUID lbp) {
        UUID pboFromToken = TokenPayloadUtil.getTokenPayload().getPbo();

        LabExamFilter labExamFilter = new LabExamFilter(date, lbp, pboFromToken);
        LabExamSpecification labExamSpecification = new LabExamSpecification(labExamFilter);

        List<ScheduledLabExam> scheduledLabExams = scheduledLabExamRepository.findAll(labExamSpecification);

        if (scheduledLabExams.isEmpty()) {
            throw new NotFoundException("Nisu pronadjeni zakazani pregledi na osnovu prosledjenih parametara.");
        }

        return labExamMapper.scheduledLabExamsToLabExamListResponse(scheduledLabExams);
    }

    @Override
    public LabExamResponse updateStatus(UpdateLabExamStatusRequest updateLabExamStatusRequest) {
        Long id = updateLabExamStatusRequest.getId();
        ExamStatus examStatus = ExamStatus.valueOfNotation(updateLabExamStatusRequest.getStatus());
        if (examStatus == null) {
            String errMessage = String.format("Pogresan status '%s'", updateLabExamStatusRequest.getStatus());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        ScheduledLabExam scheduledLabExam = scheduledLabExamRepository.findById(id).orElseThrow(() -> {
            log.error("Ne postoji laboratorijski pregled sa id-ijem '{}'", id);
            throw new NotFoundException("Pregled sa datim id-ijem ne postoji");
        });

        scheduledLabExam.setExamStatus(examStatus);
        scheduledLabExamRepository.save(scheduledLabExam);

        return labExamMapper.modelToResponse(scheduledLabExam);
    }
}
