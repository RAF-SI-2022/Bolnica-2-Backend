package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
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

    public ScheduledMedExamination updateSchedMedExamRequestToScheduledMedExamination(ScheduledMedExamination scheduledMedExamination
            , UpdateSchedMedExamRequest updateSchedMedExamRequest) {

        ExaminationStatus examinationStatus= ExaminationStatus.valueOfNotation(updateSchedMedExamRequest.getExaminationStatus());

        if(examinationStatus == null){
            String errMessage = String.format("Nepoznat status pregleda '%s'", updateSchedMedExamRequest.getExaminationStatus());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        switch (examinationStatus) {
            case ZAKAZANO:
            case OTKAZANO:
                String errMessage = String.format("Nije dozvoljeno izmeniti status pregleda  na '%s'"
                            , updateSchedMedExamRequest.getExaminationStatus());
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);

            case U_TOKU:
                scheduledMedExamination.setExaminationStatus(examinationStatus);
                scheduledMedExamination.setPatientArrivalStatus(PatientArrivalStatus.PRIMLJEN);
                break;
            case ZAVRSENO:
                scheduledMedExamination.setExaminationStatus(examinationStatus);
                scheduledMedExamination.setPatientArrivalStatus(PatientArrivalStatus.ZAVRSIO);
                break;

        }



        return scheduledMedExamination;
    }
}
