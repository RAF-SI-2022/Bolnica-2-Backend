package com.raf.si.laboratoryservice.unit.service;
import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.request.UpdateLabExamStatusRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;
import com.raf.si.laboratoryservice.exception.BadRequestException;
import com.raf.si.laboratoryservice.exception.NotFoundException;
import com.raf.si.laboratoryservice.mapper.LabExamMapper;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.repository.ScheduledLabExamRepository;
import com.raf.si.laboratoryservice.service.impl.LabExamServiceImpl;
import com.raf.si.laboratoryservice.utils.TokenPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.shaded.org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LabExamServiceTest {
    private LabExamServiceImpl labExamService;
    private ScheduledLabExamRepository scheduledLabExamRepository;
    private LabExamMapper labExamMapper;
    private ReferralRepository referralRepository;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        scheduledLabExamRepository = mock(ScheduledLabExamRepository.class);
        referralRepository = mock(ReferralRepository.class);
        labExamMapper = new LabExamMapper();
        labExamService = new LabExamServiceImpl(scheduledLabExamRepository, referralRepository, labExamMapper);
        authentication = mock(Authentication.class);
    }

    @Test
    void testCreateExamination() {
        ScheduledLabExam scheduledLabExam = createScheduledLabExam();
        CreateLabExamRequest createLabExamRequest = createLabExamRequest();
        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        tokenPayload.setPbo(UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));

        when(authentication.getPrincipal()).thenReturn(tokenPayload);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(referralRepository.findByLbp(any(UUID.class))).thenReturn(Optional.of(new Referral()));
        when(scheduledLabExamRepository.save(any(ScheduledLabExam.class))).thenReturn(scheduledLabExam);

        LabExamResponse labExamResponse = createLabExamResponse();
        LabExamResponse response = labExamService.createExamination(createLabExamRequest);
        assertEquals(response.getLbp(), labExamResponse.getLbp());
        assertEquals(response.getNote(), labExamResponse.getNote());
        assertEquals(response.getExamStatus(), labExamResponse.getExamStatus());

        verify(scheduledLabExamRepository, times(1)).save(any(ScheduledLabExam.class));
    }

    @Test
    void testGetScheduledExamCount() {
        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        tokenPayload.setPbo(UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));

        when(authentication.getPrincipal()).thenReturn(tokenPayload);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(scheduledLabExamRepository.countByPboIdAndDateRange(any(UUID.class), any(Date.class), any(Date.class))).thenReturn(5L);

        Optional<Long> response = labExamService.getScheduledExamCount(new Date());
        assertThat(response).isPresent().contains(5L);

        verify(scheduledLabExamRepository, times(1)).countByPboIdAndDateRange(any(UUID.class), any(Date.class), any(Date.class));
    }

    @Test
    void testGetScheduledExams() {
        Date date = new Date();
        UUID lbp = UUID.randomUUID();
        UUID pboFromToken = UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002");
        List<ScheduledLabExam> scheduledLabExams = createListOfScheduledExams();

        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setPbo(pboFromToken);

        when(scheduledLabExamRepository.findAll(any(Specification.class))).thenReturn(scheduledLabExams);

        List<LabExamResponse> labExamResponses = labExamService.getScheduledExams(date, lbp);

        assertThat(labExamResponses).isNotNull();
        assertThat(labExamResponses).hasSize(2);
    }

    @Test
    void testGetScheduledExams_noExamsFound() {
        Date date = new Date();
        UUID lbp = UUID.randomUUID();

        when(scheduledLabExamRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        try {
            labExamService.getScheduledExams(date, lbp);
        } catch (NotFoundException ex) {
            assertEquals( "Nisu pronadjeni zakazani pregledi na osnovu prosledjenih parametara.", ex.getMessage());
        }
    }


    @Test
    void testUpdateStatus() {
        ScheduledLabExam scheduledLabExam = new ScheduledLabExam();
        scheduledLabExam.setId(1L);
        scheduledLabExam.setExamStatus(ExamStatus.ZAKAZANO);
        UpdateLabExamStatusRequest updateLabExamStatusRequest = new UpdateLabExamStatusRequest();
        updateLabExamStatusRequest.setId(1L);
        updateLabExamStatusRequest.setStatus("Završeno");

        when(scheduledLabExamRepository.findById(1L)).thenReturn(Optional.of(scheduledLabExam));
        when(scheduledLabExamRepository.save(any(ScheduledLabExam.class))).thenReturn(scheduledLabExam);

        LabExamResponse result = labExamService.updateStatus(updateLabExamStatusRequest);

        assertNotNull(result);
        assertEquals(result.getExamStatus().name(), ExamStatus.ZAVRSENO.name());
    }

    @Test
    void testUpdateStatus_invalidStatus() {
        ScheduledLabExam scheduledLabExam = new ScheduledLabExam();
        scheduledLabExam.setId(1L);
        scheduledLabExam.setExamStatus(ExamStatus.ZAKAZANO);
        UpdateLabExamStatusRequest updateLabExamStatusRequest = new UpdateLabExamStatusRequest();
        updateLabExamStatusRequest.setId(1L);
        updateLabExamStatusRequest.setStatus("InvalidStatus");

        try {
            labExamService.updateStatus(updateLabExamStatusRequest);
        } catch (BadRequestException ex) {
            assertEquals("Pogresan status 'InvalidStatus'", ex.getMessage());
        }
    }

    @Test
    public void testRequestToModelAndModelToResponse() {
        CreateLabExamRequest request = new CreateLabExamRequest();
        request.setLbp(UUID.randomUUID());
        request.setScheduledDate(new Date());
        request.setNote("Test note");

        UUID lbz = UUID.randomUUID();
        UUID pbo = UUID.randomUUID();

        ScheduledLabExam model = labExamMapper.requestToModel(request, lbz, pbo);
        assertNotNull(model);
        assertEquals(request.getLbp(), model.getLbp());
        assertEquals(request.getScheduledDate(), model.getScheduledDate());
        assertEquals(request.getNote(), model.getNote());
        assertEquals(ExamStatus.ZAKAZANO, model.getExamStatus());
        assertEquals(lbz, model.getLbz());
        assertEquals(pbo, model.getPbo());

        LabExamResponse response = labExamMapper.modelToResponse(model);
        assertNotNull(response);
        assertEquals(model.getId(), response.getId());
        assertEquals(model.getLbp(), response.getLbp());
        assertEquals(model.getScheduledDate(), response.getScheduledDate());
        assertEquals(model.getNote(), response.getNote());
        assertEquals(model.getExamStatus(), response.getExamStatus());
        assertEquals(model.getLbz(), response.getLbz());
    }



    private CreateLabExamRequest createLabExamRequest() {
        CreateLabExamRequest createLabExamRequest = new CreateLabExamRequest();
        createLabExamRequest.setLbp(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b436"));
        createLabExamRequest.setScheduledDate(DateUtils.addDays(new Date(), 1));
        createLabExamRequest.setNote("Napomena");
        return createLabExamRequest;
    }

    private ScheduledLabExam createScheduledLabExam() {
        ScheduledLabExam scheduledLabExam = new ScheduledLabExam();
        scheduledLabExam.setLbp(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b436"));
        scheduledLabExam.setNote("Napomena");
        scheduledLabExam.setExamStatus(ExamStatus.ZAKAZANO);
        scheduledLabExam.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        scheduledLabExam.setPbo(UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));
        return scheduledLabExam;
    }

    private LabExamResponse createLabExamResponse() {
        LabExamResponse labExamResponse = new LabExamResponse();

        labExamResponse.setLbp(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b436"));
        labExamResponse.setNote("Napomena");
        labExamResponse.setExamStatus(ExamStatus.ZAKAZANO);

        return labExamResponse;
    }

    private List<ScheduledLabExam> createListOfScheduledExams() {
        List<ScheduledLabExam> scheduledLabExams = new ArrayList<>();
        ScheduledLabExam scheduledLabExam1 = new ScheduledLabExam();
        scheduledLabExam1.setId(1L);
        scheduledLabExam1.setPbo( UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));
        scheduledLabExam1.setLbp(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b436"));
        scheduledLabExam1.setScheduledDate(Timestamp.valueOf(LocalDateTime.now()));
        ScheduledLabExam scheduledLabExam2 = new ScheduledLabExam();
        scheduledLabExam2.setId(2L);
        scheduledLabExam2.setPbo( UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));
        scheduledLabExam2.setLbp(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b436"));
        scheduledLabExam2.setScheduledDate(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        
        scheduledLabExams.add(scheduledLabExam1);
        scheduledLabExams.add(scheduledLabExam2);
        return scheduledLabExams;
    }
}