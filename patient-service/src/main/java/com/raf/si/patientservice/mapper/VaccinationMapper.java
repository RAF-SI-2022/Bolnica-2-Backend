package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.request.VaccinationCovidRequest;
import com.raf.si.patientservice.dto.response.DosageReceivedResponse;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationListResponse;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationResponse;
import com.raf.si.patientservice.dto.response.VaccinationCovidResponse;
import com.raf.si.patientservice.model.AvailableTerm;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledVaccinationCovid;
import com.raf.si.patientservice.model.VaccinationCovid;
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

    public ScheduledVaccinationListResponse scheduledVaccinationPageToResponse(Page<ScheduledVaccinationCovid> scheduledTestingPage) {
        List<ScheduledVaccinationResponse> schedVaccinations = scheduledTestingPage.toList()
                .stream()
                .map(this::scheduledVaccinationToResponse)
                .collect(Collectors.toList());

        return new ScheduledVaccinationListResponse(schedVaccinations, scheduledTestingPage.getTotalElements());
    }

    public VaccinationCovid vaccCovidRequestToModel(VaccinationCovidRequest request) {
        VaccinationCovid vaccinationCovid= new VaccinationCovid();
        vaccinationCovid.setDateTime(request.getDateTime());
        vaccinationCovid.incrementDosage(request.getDoseReceived());
        return vaccinationCovid;
    }

    public VaccinationCovidResponse vaccinationCovidToResponse(VaccinationCovid  vaccinationCovid) {
        VaccinationCovidResponse resposne= new VaccinationCovidResponse();
        resposne.setId(vaccinationCovid.getId());
        resposne.setVaccine(vaccinationCovid.getVaccine().getName());
        resposne.setDoseReceived(vaccinationCovid.getDoseReceived());
        resposne.setLocalDateTime(vaccinationCovid.getDateTime());
        resposne.setScheduledVaccinationResponse(this.scheduledVaccinationToResponse(vaccinationCovid.getScheduledVaccinationCovid()));

        return  resposne;
    }

    public DosageReceivedResponse vaccinationCovidToDosageReceived(Long doseReceived) {
        DosageReceivedResponse response = new DosageReceivedResponse();
        response.setDosageReceived(doseReceived);
        return  response;
    }
}
