package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.VisitRequest;
import com.raf.si.patientservice.dto.response.VisitResponse;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.Visit;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VisitMapper {

    public Visit visitRequestToModel(UUID registerLbz, Patient patient, VisitRequest visitRequest) {
        Visit visit = new Visit();
        visit.setJMBGVisitor(visitRequest.getJmbgVisitor());
        visit.setNote(visitRequest.getNote());
        visit.setVisitorFirstName(visitRequest.getVisitorFirstName());
        visit.setVisitorLastName(visitRequest.getVisitorLastName());
        visit.setRegisterLbz(registerLbz);
        visit.setPatient(patient);

        return visit;
    }

    public VisitResponse modelToVisitResponse(Visit visit) {
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setJMBGVisitor(visit.getJMBGVisitor());
        visitResponse.setVisitDate(visit.getVisitDate());
        visitResponse.setNote(visit.getNote());
        visitResponse.setVisitorFirstName(visit.getVisitorFirstName());
        visitResponse.setVisitorLastName(visit.getVisitorLastName());
        visitResponse.setLbp(visit.getPatient().getLbp());
        visitResponse.setId(visit.getId());
        visitResponse.setRegisterLbz(visit.getRegisterLbz());

        return visitResponse;
    }
}
