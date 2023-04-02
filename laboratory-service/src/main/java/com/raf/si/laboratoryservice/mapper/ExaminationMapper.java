package com.raf.si.laboratoryservice.mapper;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExaminationMapper {
    public ScheduledLabExam requestToModel(CreateLabExamRequest createLabExamRequest, UUID lbz, UUID pbo) {
        ScheduledLabExam scheduledLabExam = new ScheduledLabExam();

        scheduledLabExam.setLbp(createLabExamRequest.getLbp());
        scheduledLabExam.setScheduledDate(createLabExamRequest.getScheduledDate());
        scheduledLabExam.setNote(createLabExamRequest.getNote());
        scheduledLabExam.setExamStatus(ExamStatus.ZAKAZANO);
        scheduledLabExam.setLbz(lbz);
        scheduledLabExam.setPbo(pbo);

        return scheduledLabExam;
    }

    public LabExamResponse modelToResponse(ScheduledLabExam scheduledLabExam) {
        LabExamResponse referralResponse = new LabExamResponse();

        referralResponse.setLbp(scheduledLabExam.getLbp());
        referralResponse.setScheduledDate(scheduledLabExam.getScheduledDate());
        referralResponse.setNote(scheduledLabExam.getNote());

        return referralResponse;
    }
}
