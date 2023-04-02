package com.raf.si.laboratoryservice.mapper;

import com.raf.si.laboratoryservice.dto.request.CreateExaminationRequest;
import com.raf.si.laboratoryservice.dto.response.ExaminationResponse;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExaminationMapper {
    public ScheduledLabExam requestToModel(CreateExaminationRequest createExaminationRequest, UUID lbz, UUID pbo) {
        ScheduledLabExam scheduledLabExam = new ScheduledLabExam();

        scheduledLabExam.setLbp(createExaminationRequest.getLbp());
        scheduledLabExam.setScheduledDate(createExaminationRequest.getScheduledDate());
        scheduledLabExam.setNote(createExaminationRequest.getNote());
        scheduledLabExam.setExamStatus(ExamStatus.ZAKAZANO);
        scheduledLabExam.setLbz(lbz);
        scheduledLabExam.setPbo(pbo);

        return scheduledLabExam;
    }

    public ExaminationResponse modelToResponse(ScheduledLabExam scheduledLabExam) {
        ExaminationResponse referralResponse = new ExaminationResponse();

        referralResponse.setLbp(scheduledLabExam.getLbp());
        referralResponse.setScheduledDate(scheduledLabExam.getScheduledDate());
        referralResponse.setNote(scheduledLabExam.getNote());

        return referralResponse;
    }
}
