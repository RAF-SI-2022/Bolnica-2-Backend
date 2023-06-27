package com.raf.si.patientservice.unit.controller;


import com.raf.si.patientservice.controller.TestingController;
import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.request.TestingRequest;
import com.raf.si.patientservice.dto.response.AvailableTermResponse;
import com.raf.si.patientservice.dto.response.ScheduledTestingListResponse;
import com.raf.si.patientservice.dto.response.ScheduledTestingResponse;
import com.raf.si.patientservice.dto.response.TestingResponse;
import com.raf.si.patientservice.service.TestingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestingControllerTest {

    @Mock
    private TestingService testingService;
    @InjectMocks
    private TestingController testingController;

    @Test
    void scheduleTesting_Success(){
        UUID lbp = UUID.randomUUID();
        ScheduledTestingRequest scheduledTestingRequest = makeScheduledTestingRequest();
        ScheduledTestingResponse response = new ScheduledTestingResponse();

        when(testingService.scheduleTesting(lbp, scheduledTestingRequest, ""))
                .thenReturn(response);

        assertEquals(testingController.scheduleTesting(lbp,scheduledTestingRequest,"")
                , ResponseEntity.ok(response));
    }

    @Test
    void getAvailableTerm_Success(){
        LocalDateTime localDateTime = LocalDateTime.now();
        AvailableTermResponse response = new AvailableTermResponse();

        when(testingService.getAvailableTerm(localDateTime,""))
                .thenReturn(response);

        assertEquals(testingController.getAvailableTerm(localDateTime,"")
                , ResponseEntity.ok(response));
    }

    @Test
    void getScheduledTestings_Success(){
        ScheduledTestingListResponse response = new ScheduledTestingListResponse();
        UUID lbp = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        when(testingService.getScheduledtestings(lbp,date, PageRequest.of(0,1)))
                .thenReturn(response);

        assertEquals(testingController.getScheduledTestings(lbp,date, 0,1)
                , ResponseEntity.ok(response));
    }

    @Test
    void createTesting_Success(){
        UUID lbp = UUID.randomUUID();
        TestingRequest request = makeCreateTesting();
        TestingResponse response = new TestingResponse();

        when(testingService.createTesting(lbp, request))
                .thenReturn(response);

        assertEquals(testingController.createTesting( lbp ,request)
                , ResponseEntity.ok(response));
    }

    @Test
    void changeTestingStatus_Success(){
        ScheduledTestingResponse response = new ScheduledTestingResponse();

        when(testingService.changeScheduledTestingStatus(1L,"",""))
                .thenReturn(response);

        assertEquals(testingController.changeTestingStatus(1L,"","")
                , ResponseEntity.ok(response));
    }

    @Test
    void deleteScheduledTesting_Success(){
        ScheduledTestingResponse response = new ScheduledTestingResponse();

        when(testingService.deleteScheduledTesting(1L))
                .thenReturn(response);

        assertEquals(testingController.deleteScheduledTesting(1L)
                , ResponseEntity.ok(response));
    }

    @Test
    void getTestingHistory_Success() {
        List<TestingResponse> result = new ArrayList<>();

        when(testingService.getTestingHistory(any()))
                .thenReturn(result);

        assertEquals(testingController.getTestingHistory(UUID.randomUUID()).getBody(),
                result);
    }

    private TestingRequest makeCreateTesting(){
        TestingRequest testingRequest = new TestingRequest();
        testingRequest.setScheduledTestingId(1L);
        testingRequest.setPulse("");
        testingRequest.setReason("");
        testingRequest.setDescription("");
        testingRequest.setTemperature("");
        testingRequest.setAppliedTherapies("");
        testingRequest.setBloodPressure("");
        testingRequest.setAppliedTherapies("");
        return  testingRequest;
    }

    private ScheduledTestingRequest  makeScheduledTestingRequest(){
        ScheduledTestingRequest scheduledTestingRequest = new ScheduledTestingRequest();
        scheduledTestingRequest.setDateAndTime(LocalDateTime.now());
        return  scheduledTestingRequest;
    }
}
