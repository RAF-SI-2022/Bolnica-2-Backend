package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationResponse;
import com.raf.si.patientservice.model.AvailableTerm;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledVaccinationCovid;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Component
public class VaccinationMapper {
    public ScheduledVaccinationCovid scheduledVaccinationRequestToModel(Patient patient, ScheduledVaccinationRequest request) {
        ScheduledVaccinationCovid scheduledVaccinationCovid = new ScheduledVaccinationCovid();
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();

        scheduledVaccinationCovid.setPatient(patient);
        scheduledVaccinationCovid.setDateAndTime(request.getDateAndTime());
        scheduledVaccinationCovid.setSchedulerLbz(tokenPayload.getLbz());
        if (request.getNote() != null) {
            scheduledVaccinationCovid.setNote(request.getNote());
        }
        return scheduledVaccinationCovid;
    }


    public AvailableTerm makeAvailableTerm(LocalDateTime dateAndTime, UUID pbo, int availableNurses) {
        AvailableTerm availableTerm = new AvailableTerm();

        availableTerm.setAvailableNursesNum(availableNurses);
        availableTerm.setPbo(pbo);
        availableTerm.setDateAndTime(dateAndTime);

        return availableTerm;
    }

    public ScheduledVaccinationResponse scheduledVaccinationToResponse(ScheduledVaccinationCovid scheduledVaccinationCovid) {
        ScheduledVaccinationResponse response = new ScheduledVaccinationResponse();

        response.setDateAndTime(scheduledVaccinationCovid.getDateAndTime());
        response.setNote(scheduledVaccinationCovid.getNote());
        response.setSchedulerLbz(scheduledVaccinationCovid.getSchedulerLbz());
        response.setTestStatus(scheduledVaccinationCovid.getTestStatus());
        response.setPatientArrivalStatus(scheduledVaccinationCovid.getPatientArrivalStatus());
        response.setId(scheduledVaccinationCovid.getId());
        response.setLbp(scheduledVaccinationCovid.getPatient().getLbp());

        return response;
    }
}
