package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.VisitRequest;
import com.raf.si.patientservice.dto.response.VisitListResponse;
import com.raf.si.patientservice.dto.response.VisitResponse;
import com.raf.si.patientservice.mapper.VisitMapper;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.Visit;
import com.raf.si.patientservice.repository.VisitRepository;
import com.raf.si.patientservice.service.HospitalizationService;
import com.raf.si.patientservice.service.VisitService;
import com.raf.si.patientservice.service.impl.VisitServiceImpl;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VisitServiceTest {

    private VisitService visitService;
    private HospitalizationService hospitalizationService;
    private VisitRepository visitRepository;
    private VisitMapper visitMapper;

    @BeforeEach
    void setup() {
        hospitalizationService = mock(HospitalizationService.class);
        visitRepository = mock(VisitRepository.class);
        visitMapper = new VisitMapper();
        visitService = new VisitServiceImpl(visitRepository, visitMapper, hospitalizationService);
        mockTokenPayloadUtil();
    }

    @AfterEach
    void cleanup() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    void createVisit_Success() {
        UUID lbp = UUID.randomUUID();
        VisitRequest visitRequest = makeVisitRequest();
        Patient patient = makePatient();

        when(hospitalizationService.getHospitalisedPatientByLbp(lbp))
                .thenReturn(patient);

        Visit visit = visitMapper.visitRequestToModel(makeTokenPayload().getLbz(),
                patient, visitRequest);

        when(visitRepository.save(any())).thenReturn(visit);

        assertEquals(visitService.createVisit(lbp, visitRequest),
                visitMapper.modelToVisitResponse(visit));
    }

    @Test
    void getVisits_Success() {
        UUID lbp = UUID.randomUUID();
        VisitRequest visitRequest = makeVisitRequest();
        Patient patient = makePatient();
        Pageable pageable = PageRequest.of(0, 5);

        Visit visit = visitMapper.visitRequestToModel(makeTokenPayload().getLbz(),
                patient, visitRequest);
        Page<Visit> page = new PageImpl<>(Collections.singletonList(visit));


        when(visitRepository.getAllVisitsByPatientLbp(lbp, pageable))
                .thenReturn(page);

        List<VisitResponse> responseList = page.map(visitMapper::modelToVisitResponse)
                .stream()
                .collect(Collectors.toList());

        assertEquals(visitService.getVisits(lbp, pageable),
                new VisitListResponse(responseList, page.getTotalElements()));
    }

    private Patient makePatient() {
        Patient patient = new Patient();
        long id = 1;

        patient.setId(id);
        patient.setLbp(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        patient.setFirstName("Ime");
        patient.setLastName("Prezime");
        patient.setJmbg("512312311231");
        patient.setBirthDate(new Date());

        return patient;
    }

    private VisitRequest makeVisitRequest() {
        VisitRequest visitRequest = new VisitRequest();
        visitRequest.setVisitorFirstName("firstName");
        visitRequest.setVisitorLastName("lastName");
        visitRequest.setJmbgVisitor("051234120121");
        visitRequest.setNote("note");

        return visitRequest;
    }

    private void mockTokenPayloadUtil() {
        Mockito.mockStatic(TokenPayloadUtil.class);

        TokenPayload tokenPayload = makeTokenPayload();

        when(TokenPayloadUtil.getTokenPayload())
                .thenReturn(tokenPayload);
    }

    private TokenPayload makeTokenPayload() {
        TokenPayload tokenPayload = new TokenPayload();

        tokenPayload.setPbo(UUID.randomUUID());
        tokenPayload.setLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));

        return tokenPayload;
    }
}
