package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedMedExamMapper {


    public ScheduledMedExamination schedMedExamRequestToScheduledMedExamination(ScheduledMedExamination scheduledMedExamination
            ,SchedMedExamRequest schedMedExamRequest){
        scheduledMedExamination.setLbp(schedMedExamRequest.getLbp());
        scheduledMedExamination.setLbz_doctor(schedMedExamRequest.getLbz_doctor());
        scheduledMedExamination.setAppointmentDate(schedMedExamRequest.getAppointmentDate());
        scheduledMedExamination.setLbz_nurse(schedMedExamRequest.getLbz_nurse());


        if (schedMedExamRequest.getNote() != null)
            scheduledMedExamination.setNote(schedMedExamRequest.getNote());



        return  scheduledMedExamination;
    }

    public SchedMedExamResponse scheduledMedExaminationToSchedMedExamResponse(ScheduledMedExamination scheduledMedExamination) {
        SchedMedExamResponse schedMedExamResponse= new SchedMedExamResponse();

        schedMedExamResponse.setId(scheduledMedExamination.getId());
        schedMedExamResponse.setLbp(scheduledMedExamination.getLbp());
        schedMedExamResponse.setLbz_doctor(scheduledMedExamination.getLbz_doctor());
        schedMedExamResponse.setAppointmentDate(scheduledMedExamination.getAppointmentDate());
        schedMedExamResponse.setExaminationStatus(scheduledMedExamination.getExaminationStatus());
        schedMedExamResponse.setPatientArrivalStatus(scheduledMedExamination.getPatientArrivalStatus());
        schedMedExamResponse.setNote(scheduledMedExamination.getNote());
        schedMedExamResponse.setLbz_nurse(scheduledMedExamination.getLbz_nurse());

        return schedMedExamResponse;
    }
}
