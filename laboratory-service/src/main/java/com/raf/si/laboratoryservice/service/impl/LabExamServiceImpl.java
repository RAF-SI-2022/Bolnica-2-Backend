package com.raf.si.laboratoryservice.service.impl;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.request.UpdateLabExamStatusRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;
import com.raf.si.laboratoryservice.exception.NotFoundException;
import com.raf.si.laboratoryservice.mapper.LabExamMapper;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;
import com.raf.si.laboratoryservice.repository.ScheduledLabExamRepository;
import com.raf.si.laboratoryservice.service.LabExamService;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public List<LabExamResponse> getScheduledExams(Timestamp date, UUID lbp) {
        UUID pboFromToken = TokenPayloadUtil.getTokenPayload().getPbo();
        List<ScheduledLabExam> scheduledLabExams;

        if (date == null && lbp == null) {
            scheduledLabExams = scheduledLabExamRepository.findByPbo(pboFromToken);
        } else if (date != null && lbp != null) {
            LocalDateTime localDateTime = date.toLocalDateTime().plusDays(1).withHour(0).withMinute(0).withSecond(0);
            Timestamp endDate = Timestamp.valueOf(localDateTime);
            scheduledLabExams = scheduledLabExamRepository.findByPboAndDateRangeAndLbp(pboFromToken, date, endDate, lbp);
        } else if (date != null) {
            LocalDateTime localDateTime = date.toLocalDateTime().plusDays(1).withHour(0).withMinute(0).withSecond(0);
            Timestamp endDate = Timestamp.valueOf(localDateTime);
            scheduledLabExams = scheduledLabExamRepository.findByPboAndDateRange(pboFromToken, date, endDate);
        } else {
            scheduledLabExams = scheduledLabExamRepository.findByPboAndLbp(pboFromToken, lbp);
        }

        if (scheduledLabExams.isEmpty()) {
            throw new NotFoundException("Nisu pronadjeni zakazani pregledi na osnovu prosledjenih parametara.");
        }

        return labExamMapper.scheduledLabExamsToLabExamListResponse(scheduledLabExams);
    }

    @Override
    public LabExamResponse updateStatus(UpdateLabExamStatusRequest updateLabExamStatusRequest) {
        Long id = updateLabExamStatusRequest.getId();
        ExamStatus status = updateLabExamStatusRequest.getStatus();
        ScheduledLabExam scheduledLabExam = scheduledLabExamRepository.findById(id).orElseThrow(() -> {
            log.error("Ne postoji laboratorijski pregled sa id-ijem '{}'", id);
            throw new NotFoundException("Pregled sa datim id-ijem ne postoji");
        });

        scheduledLabExam.setExamStatus(status);
        scheduledLabExamRepository.save(scheduledLabExam);

        return labExamMapper.modelToResponse(scheduledLabExam);
    }
}
