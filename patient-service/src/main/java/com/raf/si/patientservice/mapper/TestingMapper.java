package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.response.AvailableTermResponse;
import com.raf.si.patientservice.dto.response.ScheduledTestingResponse;
import com.raf.si.patientservice.model.AvailableTerm;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledTesting;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class TestingMapper {

    private final PatientMapper patientMapper;

    public TestingMapper(PatientMapper patientMapper) {
        this.patientMapper = patientMapper;
    }

    public ScheduledTesting scheduledTestingRequestToModel(Patient patient, ScheduledTestingRequest request) {
        ScheduledTesting scheduledTesting = new ScheduledTesting();
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();

        scheduledTesting.setPatient(patient);
        scheduledTesting.setDateAndTime(request.getDateAndTime());
        scheduledTesting.setSchedulerLbz(tokenPayload.getLbz());
        if (request.getNote() != null) {
            scheduledTesting.setNote(request.getNote());
        }

        return scheduledTesting;
    }

    public AvailableTerm makeAvailableTerm(Date dateAndTime, UUID pbo, int availableNurses) {
        AvailableTerm availableTerm = new AvailableTerm();

        availableTerm.setAvailableNursesNum(availableNurses);
        availableTerm.setPbo(pbo);
        availableTerm.setDateAndTime(dateAndTime);

        return availableTerm;
    }

    public ScheduledTestingResponse scheduledTestingToResponse(ScheduledTesting scheduledTesting) {
        ScheduledTestingResponse response = new ScheduledTestingResponse();

        response.setDateAndTime(scheduledTesting.getDateAndTime());
        response.setNote(scheduledTesting.getNote());
        response.setSchedulerLbz(scheduledTesting.getSchedulerLbz());
        response.setTestStatus(scheduledTesting.getTestStatus());
        response.setPatientArrivalStatus(scheduledTesting.getPatientArrivalStatus());
        response.setId(scheduledTesting.getId());
        response.setPatientResponse(patientMapper.patientToPatientResponse(scheduledTesting.getPatient()));

        return response;
    }

    public AvailableTermResponse availableTermToRespons(AvailableTerm availableTerm) {
        AvailableTermResponse response = new AvailableTermResponse();

        response.setAvailability(availableTerm.getAvailability());
        response.setPbo(availableTerm.getPbo());
        response.setAvailableNursesNum(availableTerm.getAvailableNursesNum());
        response.setScheduledTermsNum(availableTerm.getScheduledTermsNum());
        response.setDateAndTime(availableTerm.getDateAndTime());

        return response;
    }
}
