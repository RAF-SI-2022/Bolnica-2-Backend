package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.request.TestingRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TestingMapper {

    private final PatientMapper patientMapper;
    private final HospitalizationMapper hospitalizationMapper;

    public TestingMapper(PatientMapper patientMapper, HospitalizationMapper hospitalizationMapper) {
        this.patientMapper = patientMapper;
        this.hospitalizationMapper = hospitalizationMapper;
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

    public AvailableTerm makeAvailableTerm(LocalDateTime dateAndTime, UUID pbo, int availableNurses) {
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

    public AvailableTermResponse availableTermToResponse(AvailableTerm availableTerm) {
        AvailableTermResponse response = new AvailableTermResponse();

        response.setId(availableTerm.getId());
        response.setAvailability(availableTerm.getAvailability());
        response.setPbo(availableTerm.getPbo());
        response.setAvailableNursesNum(availableTerm.getAvailableNursesNum());
        response.setScheduledTermsNum(availableTerm.getScheduledTermsNum());
        response.setDateAndTime(availableTerm.getDateAndTime());

        return response;
    }

    public ScheduledTestingListResponse scheduledTestingPageToResponse(Page<ScheduledTesting> scheduledTestingPage) {
        List<ScheduledTestingResponse> schedTestings = scheduledTestingPage.toList()
                .stream()
                .map(this::scheduledTestingToResponse)
                .collect(Collectors.toList());

        return new ScheduledTestingListResponse(schedTestings, scheduledTestingPage.getTotalElements());
    }

    public Testing testingRequestToTesting(Patient patient, TestingRequest request) {
        Testing testing = new Testing();

        testing.setPatient(patient);
        testing.setReason(request.getReason());

        return testing;
    }

    public PatientCondition testingRequestToPatientCondition(Patient patient, TestingRequest request) {
        PatientCondition patientCondition = new PatientCondition();
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();

        patientCondition.setPatient(patient);
        patientCondition.setOnRespirator(false);
        patientCondition.setDescription(request.getDescription());
        patientCondition.setPulse(request.getPulse());
        patientCondition.setBloodPressure(request.getBloodPressure());
        patientCondition.setTemperature(request.getTemperature());
        patientCondition.setAppliedTherapies(request.getAppliedTherapies());
        patientCondition.setCollectedInfoDate(request.getCollectedInfoDate());
        patientCondition.setRegisterLbz(tokenPayload.getLbz());

        return patientCondition;
    }

    public TestingResponse testingToResponse(Testing testing) {
        return testingToResponse(testing, testing.getPatientCondition());
    }

    public TestingResponse testingToResponse(Testing testing, PatientCondition patientCondition) {
        TestingResponse response = new TestingResponse();
        PatientConditionResponse patientConditionResponse = hospitalizationMapper.patientConditionToPatientConditionResponse(patientCondition);

        response.setId(testing.getId());
        response.setReason(testing.getReason());
        response.setTestResult(testing.getTestResult());
        response.setDeleted(testing.getDeleted());
        response.setDateAndTimeOfTesting(testing.getDateAndTime());
        response.setPatientCondition(patientConditionResponse);

        return response;
    }
}
