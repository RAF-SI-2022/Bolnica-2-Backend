package com.raf.si.patientservice.unit.controller;

import com.raf.si.patientservice.controller.VisitController;
import com.raf.si.patientservice.dto.request.VisitRequest;
import com.raf.si.patientservice.dto.response.VisitListResponse;
import com.raf.si.patientservice.dto.response.VisitResponse;
import com.raf.si.patientservice.service.VisitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitControllerTest {

    @Mock
    private VisitService visitService;
    @InjectMocks
    private VisitController visitController;


    @Test
    void createVisit() {
        UUID lbp = UUID.randomUUID();
        VisitRequest visitRequest = makeVisitRequest();
        VisitResponse visitResponse = makeVisitResponse(visitRequest);

        when(visitService.createVisit(lbp, visitRequest))
                .thenReturn(visitResponse);

        assertEquals(visitController.createVisit(lbp, visitRequest).getBody(),
                visitResponse);
    }

    @Test
    void getVisitsForPatient() {
        UUID lbp = UUID.randomUUID();
        VisitRequest visitRequest = makeVisitRequest();
        VisitResponse visitResponse = makeVisitResponse(visitRequest);
        VisitListResponse visitListResponse = new VisitListResponse(
                Collections.singletonList(visitResponse),
                1L
        );

        when(visitService.getVisits(lbp, PageRequest.of(0, 5)))
                .thenReturn(visitListResponse);

        assertEquals(visitController.getVisitsForPatient(lbp, 0, 5).getBody(),
                visitListResponse);
    }

    private VisitRequest makeVisitRequest() {
        VisitRequest visitRequest = new VisitRequest();
        visitRequest.setVisitorFirstName("firstName");
        visitRequest.setVisitorLastName("lastName");
        visitRequest.setJmbgVisitor("051234120121");
        visitRequest.setNote("note");

        return visitRequest;
    }

    private VisitResponse makeVisitResponse(VisitRequest visitRequest) {
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setVisitorFirstName(visitRequest.getVisitorFirstName());
        visitResponse.setVisitorLastName(visitRequest.getVisitorLastName());
        visitResponse.setNote(visitRequest.getNote());
        visitResponse.setJMBGVisitor(visitRequest.getJmbgVisitor());

        return visitResponse;
    }
}
