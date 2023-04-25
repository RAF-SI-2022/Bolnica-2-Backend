package com.raf.si.laboratoryservice.unit.controller;

import com.raf.si.laboratoryservice.controller.LabExamController;
import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.request.UpdateLabExamStatusRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;
import com.raf.si.laboratoryservice.exception.BadRequestException;
import com.raf.si.laboratoryservice.exception.NotFoundException;
import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;
import com.raf.si.laboratoryservice.service.LabExamService;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.AssertionErrors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class LabExamControllerTest {

    private LabExamController labExamController;
    private LabExamService labExamService;

    @BeforeEach
    public void setUp() {
        labExamService = mock(LabExamService.class);
        labExamController = new LabExamController(labExamService);
    }

    @Test
    public void createExamination_Success() {
        CreateLabExamRequest createLabExamRequest = createLabExamRequest();
        LabExamResponse labExamResponse = new LabExamResponse();
        when(labExamService.createExamination(createLabExamRequest)).thenReturn(labExamResponse);

        ResponseEntity<LabExamResponse> responseEntity = labExamController.createExamination(createLabExamRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(labExamResponse, responseEntity.getBody());
    }

    @Test
    public void getScheduledExamCount_Success() {
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        Long scheduledExamCount = 5L;
        when(labExamService.getScheduledExamCount(date)).thenReturn(Optional.of(scheduledExamCount));

        ResponseEntity<Optional<Long>> responseEntity = labExamController.getScheduledExamCount(date);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Optional.of(scheduledExamCount), responseEntity.getBody());
    }

    @Test
    public void getScheduledExamCount_withNullParams() {
        try {
            labExamController.getScheduledExamCount(null);
        } catch (BadRequestException ex) {
            assertEquals("Pogresan zahtev.", ex.getMessage());
        }
    }

    @Test
    public void getScheduledExams_Success() {
        Timestamp date = Timestamp.valueOf(LocalDateTime.now());
        UUID lbp = UUID.randomUUID();
        List<LabExamResponse> scheduledExams = new ArrayList<>();
        when(labExamService.getScheduledExams(date, lbp)).thenReturn(scheduledExams);

        ResponseEntity<List<LabExamResponse>> responseEntity = labExamController.getScheduledExams(date, lbp);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(scheduledExams, responseEntity.getBody());
    }

    @Test
    void testGetScheduledExams_withNullParams() {
        try {
            labExamController.getScheduledExams(null, null);
        } catch (BadRequestException ex) {
            assertEquals("Pogresan zahtev.", ex.getMessage());
        }
    }


    @Test
    void testGetScheduledExams_nullDate() {
        Date date = null;
        UUID lbp = UUID.randomUUID();
        try {
            labExamService.getScheduledExams(date, lbp);
        } catch (BadRequestException ex) {
            assertEquals("Pogresan zahtev.", ex.getMessage());
        }
    }

    @Test
    void testGetScheduledExams_nullLbpId() {
        Date date = new Date();
        UUID lbp = null;
        try {
            labExamService.getScheduledExams(date, lbp);
        } catch (BadRequestException ex) {
            assertEquals("LBP ID nije validan.", ex.getMessage());
        }
    }


    @Test
    public void updateStatus_Success() {
        UpdateLabExamStatusRequest updateLabExamStatusRequest = updateLabExamStatusRequest();
        LabExamResponse labExamResponse = new LabExamResponse();
        when(labExamService.updateStatus(updateLabExamStatusRequest)).thenReturn(labExamResponse);

        ResponseEntity<LabExamResponse> responseEntity = labExamController.updateStatus(updateLabExamStatusRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(labExamResponse, responseEntity.getBody());
    }

    private CreateLabExamRequest createLabExamRequest() {
        CreateLabExamRequest createLabExamRequest = new CreateLabExamRequest();
        createLabExamRequest.setLbp(UUID.randomUUID());
        createLabExamRequest.setScheduledDate(Timestamp.valueOf(LocalDateTime.now()));

        return createLabExamRequest;
    }

    private UpdateLabExamStatusRequest updateLabExamStatusRequest() {
        UpdateLabExamStatusRequest updateLabExamStatusRequest = new UpdateLabExamStatusRequest();
        updateLabExamStatusRequest.setId(1L);
        updateLabExamStatusRequest.setStatus(String.valueOf(ExamStatus.ZAVRSENO));

        return updateLabExamStatusRequest;
    }
}

