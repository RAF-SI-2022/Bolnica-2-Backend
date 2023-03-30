package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.SchedMedExamMapper;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.repository.ScheduledMedExamRepository;
import com.raf.si.patientservice.service.SchedMedExaminationService;
import com.raf.si.patientservice.service.impl.SchedMedExaminationServiceImpl;
import com.raf.si.patientservice.utils.HttpUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;


public class SchedMedExaminationServiceTest {

    private PatientRepository patientRepository;
    private ScheduledMedExamRepository scheduledMedExamRepository;
    private SchedMedExamMapper schedMedExamMapper;
    private SchedMedExaminationService schedMedExaminationService;
    //kako se vrednost 44 injectuje tek prilikom pokretanja servisa, default vredonst tokom
    //testrianja bice 0.
    private final int DURATION_OF_EXAM= 0;

    @BeforeEach
    public void setUp(){
        patientRepository= mock(PatientRepository.class);
        scheduledMedExamRepository= mock(ScheduledMedExamRepository.class);
        schedMedExamMapper= new SchedMedExamMapper();
        schedMedExaminationService=new SchedMedExaminationServiceImpl(scheduledMedExamRepository
                , patientRepository, schedMedExamMapper);
    }

    @Test
    public void createSchedMedExam_WhenDoctorHasUncompletedExams_ThrowBadRequestException(){
        SchedMedExamRequest schedMedExamRequest= createSchedMedExamRequest();

        Date timeBetweenAppointmnets = new Date(schedMedExamRequest.getAppointmentDate().getTime()
                - DURATION_OF_EXAM * 60 * 1000);

        when(scheduledMedExamRepository.findByAppointmentDateBetweenAndLbzDoctor(timeBetweenAppointmnets,
                schedMedExamRequest.getAppointmentDate(), schedMedExamRequest.getLbzDoctor()))
                .thenReturn(Optional.of(List.of(new ScheduledMedExamination())));

        // The log shows that the DURATION_OF_EXAM is zero, but while service is running
        // the exam has an appropriate duration value.
        assertThrows(BadRequestException.class, () -> schedMedExaminationService.createSchedMedExamination(schedMedExamRequest));
    }

    @Test
    public void createSchedMedExam_WhenDoctorWithGivenLbzNotExists_ThrowBadRequestException(){
        /**
         * #TODO
         */
    }

    @Test
    public  void createSchedMedExam_WhenPatientWithGivenLbpNotExists_ThrowBadRequestException(){
        SchedMedExamRequest schedMedExamRequest= createSchedMedExamRequest();

        Date timeBetweenAppointmnets = new Date(schedMedExamRequest.getAppointmentDate().getTime() - DURATION_OF_EXAM * 60 * 1000);

        when(scheduledMedExamRepository.findByAppointmentDateBetweenAndLbzDoctor(timeBetweenAppointmnets
                ,schedMedExamRequest.getAppointmentDate(), schedMedExamRequest.getLbzDoctor()))
                .thenReturn(Optional.of(new ArrayList<>()));

        assertThrows(BadRequestException.class, () -> schedMedExaminationService.createSchedMedExamination(schedMedExamRequest));

    }

    @Test
    public void createSchedMedExam_Success(){
        SchedMedExamRequest schedMedExamRequest= createSchedMedExamRequest();

        Date timeBetweenAppointmnets = new Date(schedMedExamRequest.getAppointmentDate().getTime() - DURATION_OF_EXAM * 60 * 1000);

        when(scheduledMedExamRepository.findByAppointmentDateBetweenAndLbzDoctor(timeBetweenAppointmnets
                ,schedMedExamRequest.getAppointmentDate(), schedMedExamRequest.getLbzDoctor()))
                .thenReturn(Optional.of(new ArrayList<>()));

        when(patientRepository.findByLbp(schedMedExamRequest.getLbp())).thenReturn(Optional.of(new Patient()));

        ScheduledMedExamination scheduledMedExamination= schedMedExamMapper
                .schedMedExamRequestToScheduledMedExamination(new ScheduledMedExamination(),schedMedExamRequest);

        when(scheduledMedExamRepository.save(any())).thenReturn(scheduledMedExamination);

        assertEquals(schedMedExaminationService.createSchedMedExamination(schedMedExamRequest)
                ,schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination));
    }

    @Test
    public void updateSchedMedExaminationExamStatus_WhenGivenExamIdNotExists_ThrowBadRequestError(){
        UpdateSchedMedExamRequest updateSchedMedExamRequest = createUpdateSchedMedExamRequest("Završeno");

        assertThrows(BadRequestException.class, ()-> schedMedExaminationService
                .updateSchedMedExaminationExamStatus(updateSchedMedExamRequest));
    }

    @Test
    public void updateSchedMedExaminationExamStatus_WhenGivenUnidentifiedStatus_ThrowBadRequestError(){
        UpdateSchedMedExamRequest updateSchedMedExamRequest = createUpdateSchedMedExamRequest("foo");

        when(scheduledMedExamRepository.findById(updateSchedMedExamRequest.getId())).thenReturn(Optional
                .of(new ScheduledMedExamination()));

        assertThrows(BadRequestException.class, ()-> schedMedExaminationService
                .updateSchedMedExaminationExamStatus(updateSchedMedExamRequest));
    }

    @Test
    public void updateSchedMedExaminationExamStatus_WhenGivenForbiddenStatus_ThrowBadRequestError(){
        UpdateSchedMedExamRequest updateSchedMedExamRequest = createUpdateSchedMedExamRequest("Otkazano");

        when(scheduledMedExamRepository.findById(updateSchedMedExamRequest.getId())).thenReturn(Optional
                .of(new ScheduledMedExamination()));

        assertThrows(BadRequestException.class, ()-> schedMedExaminationService
                .updateSchedMedExaminationExamStatus(updateSchedMedExamRequest));
    }

    @Test
    public void updateSchedMedExaminationExamStatus_Success(){
        UpdateSchedMedExamRequest updateSchedMedExamRequest = createUpdateSchedMedExamRequest("U toku");
        ScheduledMedExamination scheduledMedExamination= new ScheduledMedExamination();

        when(scheduledMedExamRepository.findById(updateSchedMedExamRequest.getId())).thenReturn(Optional
                .of(scheduledMedExamination));

        scheduledMedExamination= schedMedExamMapper.updateSchedMedExamRequestToScheduledMedExaminationExamStatus
                (scheduledMedExamination,updateSchedMedExamRequest);

        when(scheduledMedExamRepository.save(any())).thenReturn(scheduledMedExamination);

        assertEquals(schedMedExaminationService.updateSchedMedExaminationExamStatus(updateSchedMedExamRequest),
                schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination));
    }

    @Test
    public void deleteSchedMedExamination_WhenGivenExamIdNotExists_ThrowBadRequestError(){
        Long id= 1L;

        assertThrows(BadRequestException.class, () -> schedMedExaminationService.deleteSchedMedExamination(id));
    }

    @Test
    public void deleteSchedMedExamination_Success(){
        Long id= 1L;
        ScheduledMedExamination scheduledMedExamination= new ScheduledMedExamination();

        when(scheduledMedExamRepository.findById(id)).thenReturn(Optional.of(scheduledMedExamination));

        doNothing().when(scheduledMedExamRepository).delete(any(ScheduledMedExamination.class));

        assertEquals(schedMedExaminationService.deleteSchedMedExamination(id)
                , schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination));
    }

    @Test
    public void getSchedMedExaminationByLbz_WhenConnectionWIthUserServiceError_ThrowInternalServerError(){
        UUID lbz= UUID.fromString("01d30a14-ce77-11ed-afa1-0242ac120002");
        Date appointmentDate= new Date();
        String token= "Bearer woauhruoawbhfupaw";
        Pageable pageable= PageRequest.of(0,5);

        Mockito.framework().clearInlineMocks();
        setUp();

        assertThrows(InternalServerErrorException.class, () -> schedMedExaminationService.getSchedMedExaminationByLbz(lbz
                , appointmentDate, token, pageable));
    }
    @Test
    public void getSchedMedExaminationByLbz_WhenGivenLbzNotExists_ThrowBadRequestError(){
        UUID lbz= UUID.fromString("01d30a14-ce77-11ed-afa1-0242ac120002");
        Date appointmentDate= new Date();
        String token= "Bearer woauhruoawbhfupaw";
        Pageable pageable= PageRequest.of(0,5);

        Mockito.framework().clearInlineMocks();
        setUp();

        mockConnectionWithUserService(-1, HttpStatus.BAD_REQUEST);

        assertThrows(BadRequestException.class, () -> schedMedExaminationService.getSchedMedExaminationByLbz(lbz
                , appointmentDate, token, pageable));
    }

    @Test
    public void getSchedMedExaminationByLbz_WhenGivenLbzNotADoctor_ThrowBadRequestError(){
        UUID lbz= UUID.fromString("01d30a14-ce77-11ed-afa1-0242ac120002");
        Date appointmentDate= new Date();
        String token= "Bearer woauhruoawbhfupaw";
        Pageable pageable= PageRequest.of(0,5);

        Mockito.framework().clearInlineMocks();
        setUp();

        mockConnectionWithUserService(-1, HttpStatus.OK);

        assertThrows(BadRequestException.class, () -> schedMedExaminationService.getSchedMedExaminationByLbz(lbz
                , appointmentDate, token, pageable));
    }

    @Test
    public void getSchedMedExaminationByLbz_Success(){
        UUID lbz= UUID.fromString("01d30a14-ce77-11ed-afa1-0242ac120002");
        Date appointmentDate= new Date();
        String token= "Bearer woauhruoawbhfupaw";
        Pageable pageable= PageRequest.of(0,5);

        Mockito.framework().clearInlineMocks();
        setUp();

        mockConnectionWithUserService(1, HttpStatus.OK);

        Page<ScheduledMedExamination> medExaminationMockPage= mock(Page.class);

        when(medExaminationMockPage.getContent()).thenReturn(List.of(new ScheduledMedExamination()));
        when(scheduledMedExamRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(medExaminationMockPage);

        assertEquals(schedMedExaminationService.getSchedMedExaminationByLbz(lbz,appointmentDate,token, pageable)
                , schedMedExamMapper.schedMedExamPageToSchedMedExamListResponse(medExaminationMockPage));
    }

    @Test
    public void updateSchedMedExaminationPatientArrivalStatus_WhenGivenIdNotExists_ThrowBadRequestError(){
        UpdateSchedMedExamRequest updateSchedMedExamRequest= createUpdateSchedMedExamRequest("foo");

        assertThrows(BadRequestException.class, () -> schedMedExaminationService.updateSchedMedExaminationPatientArrivalStatus
                (updateSchedMedExamRequest));
    }

    @Test
    public void updateSchedMedExaminationPatientArrivalStatus_WhenGivenUnidentifiedStatus_ThrowBadRequestError(){
        UpdateSchedMedExamRequest updateSchedMedExamRequest= createUpdateSchedMedExamRequest("foo");

        when(scheduledMedExamRepository.findById(updateSchedMedExamRequest.getId())).thenReturn(
                Optional.of(new ScheduledMedExamination()));

        assertThrows(BadRequestException.class, () -> schedMedExaminationService.updateSchedMedExaminationPatientArrivalStatus
                (updateSchedMedExamRequest));
    }

    @Test
    public void  updateSchedMedExaminationPatientArrivalStatus_Success() {
        UpdateSchedMedExamRequest updateSchedMedExamRequest= createUpdateSchedMedExamRequest("Čeka");
        ScheduledMedExamination scheduledMedExamination= new ScheduledMedExamination();

        when(scheduledMedExamRepository.findById(updateSchedMedExamRequest.getId())).thenReturn(
                Optional.of(scheduledMedExamination));

        scheduledMedExamination= schedMedExamMapper.updateSchedMedExamRequestToScheduledMedExaminationPatientArrivalStatus
                (scheduledMedExamination,updateSchedMedExamRequest);

        when(scheduledMedExamRepository.save(any())).thenReturn(scheduledMedExamination);

        assertEquals(schedMedExaminationService.updateSchedMedExaminationPatientArrivalStatus(updateSchedMedExamRequest)
                , schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination));
    }

    private  void mockConnectionWithUserService(int isDoctor, HttpStatus status){
        Mockito.mockStatic(HttpUtils.class);
        List<String> permissionsSpy = Mockito.spy(new ArrayList<>());

        doReturn(isDoctor).when(permissionsSpy).indexOf("ROLE_DR_SPEC_ODELJENJA");
        doReturn(-1).when(permissionsSpy).indexOf("ROLE_DR_SPEC");
        doReturn(-1).when(permissionsSpy).indexOf("ROLE_DR_SPEC_POV");

        UserResponse userResponseMock = Mockito.mock(UserResponse.class);
        when(userResponseMock.getPermissions()).thenReturn(permissionsSpy);

        ResponseEntity<UserResponse> responseBodyMock = Mockito.mock(ResponseEntity.class);
        when(responseBodyMock.getBody()).thenReturn(userResponseMock);

        doReturn(status).when(responseBodyMock).getStatusCode();

        when(HttpUtils.findUserByLbz(any(String.class),any(UUID.class))).thenReturn(responseBodyMock);
    }


    private SchedMedExamRequest createSchedMedExamRequest(){
        SchedMedExamRequest schedMedExamRequest= new SchedMedExamRequest();
        schedMedExamRequest.setLbp(UUID.fromString("01d30a14-ce77-11ed-afa1-0242ac120002"));
        schedMedExamRequest.setLbzDoctor(UUID.fromString("11d47e16-ce77-11ed-afa1-0242ac120002"));
        schedMedExamRequest.setAppointmentDate(new Date());
        schedMedExamRequest.setNote("Note");
        schedMedExamRequest.setLbzNurse(UUID.fromString("2024a3b0-ce77-11ed-afa1-0242ac120002"));

        return  schedMedExamRequest;
    }

    private UpdateSchedMedExamRequest createUpdateSchedMedExamRequest(String status){
        UpdateSchedMedExamRequest updateSchedMedExamRequest=new UpdateSchedMedExamRequest();
        updateSchedMedExamRequest.setId(1L);
        updateSchedMedExamRequest.setNewStatus(status);
        return updateSchedMedExamRequest;
    }
}