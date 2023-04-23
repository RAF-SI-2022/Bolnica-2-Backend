package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.PatientMapper;
import com.raf.si.patientservice.mapper.SchedMedExamMapper;
import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.model.enums.patient.*;
import com.raf.si.patientservice.repository.*;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.SchedMedExaminationService;
import com.raf.si.patientservice.service.impl.PatientServiceImpl;
import com.raf.si.patientservice.service.impl.SchedMedExaminationServiceImpl;
import com.raf.si.patientservice.utils.HttpUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;


import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;


public class SchedMedExaminationServiceTest {

    private PatientService patientService;
    private  PatientRepository patientRepository;
    private ScheduledMedExamRepository scheduledMedExamRepository;
    private SchedMedExamMapper schedMedExamMapper;
    private SchedMedExaminationService schedMedExaminationService;
    private JdbcLockRegistry lockRegistry;
    //kako se vrednost 44 injectuje tek prilikom pokretanja servisa, default vredonst tokom
    //testrianja bice 0.
    private final int DURATION_OF_EXAM= 0;

    @BeforeEach
    public void setUp(){
        patientRepository = mock(PatientRepository.class);
        scheduledMedExamRepository= mock(ScheduledMedExamRepository.class);
        PatientMapper patientMapper = new PatientMapper();
        schedMedExamMapper= new SchedMedExamMapper(patientMapper);
        lockRegistry = mock(JdbcLockRegistry.class);
        patientService= new PatientServiceImpl(patientRepository,
                mock(HealthRecordRepository.class) ,
                mock(VaccinationRepository.class),
                mock(OperationRepository.class),
                mock(MedicalHistoryRepository.class),
                mock(MedicalExaminationRepository.class),
                mock(AllergyRepository.class),
                patientMapper);
        schedMedExaminationService=new SchedMedExaminationServiceImpl(scheduledMedExamRepository
                , patientService, schedMedExamMapper, lockRegistry);
    }
    @AfterEach
    public void clear(){
        Mockito.framework().clearInlineMocks();
    }

    @Test
    public void createSchedMedExam_WhenDoctorHasUncompletedExams_ThrowBadRequestException(){
        SchedMedExamRequest schedMedExamRequest= createSchedMedExamRequest();
        String token= "Bearer woauhruoawbhfupaw";

        Date timeBetweenAppointmnets = new Date(schedMedExamRequest.getAppointmentDate().getTime()
                - DURATION_OF_EXAM * 60 * 1000);

        when(scheduledMedExamRepository.findByAppointmentDateBetweenAndLbzDoctor(timeBetweenAppointmnets,
                schedMedExamRequest.getAppointmentDate(), schedMedExamRequest.getLbzDoctor()))
                .thenReturn(Optional.of(List.of(new ScheduledMedExamination())));

        // The log shows that the DURATION_OF_EXAM is zero, but while service is running
        // the exam has an appropriate duration value.
        assertThrows(BadRequestException.class, () -> schedMedExaminationService.createSchedMedExaminationLocked(schedMedExamRequest
                , token));
    }

    @Test
    public void createSchedMedExam_WhenDoctorWithGivenLbzNotExists_ThrowBadRequestException(){
        SchedMedExamRequest schedMedExamRequest= createSchedMedExamRequest();
        String token= "Bearer woauhruoawbhfupaw";

        Date timeBetweenAppointmnets = new Date(schedMedExamRequest.getAppointmentDate().getTime() - DURATION_OF_EXAM * 60 * 1000);

        Mockito.framework().clearInlineMocks();
        setUp();

        when(scheduledMedExamRepository.findByAppointmentDateBetweenAndLbzDoctor(timeBetweenAppointmnets
                ,schedMedExamRequest.getAppointmentDate(), schedMedExamRequest.getLbzDoctor()))
                .thenReturn(Optional.of(new ArrayList<>()));

        mockConnectionWithUserService(-1, HttpStatus.BAD_REQUEST);

        assertThrows(BadRequestException.class, () -> schedMedExaminationService.createSchedMedExaminationLocked(schedMedExamRequest
                , token));

    }

    @Test
    public  void createSchedMedExam_WhenPatientWithGivenLbpNotExists_ThrowBadRequestException(){
        SchedMedExamRequest schedMedExamRequest= createSchedMedExamRequest();
        String token= "Bearer woauhruoawbhfupaw";

        Date timeBetweenAppointmnets = new Date(schedMedExamRequest.getAppointmentDate().getTime() - DURATION_OF_EXAM * 60 * 1000);

        Mockito.framework().clearInlineMocks();
        setUp();

        when(scheduledMedExamRepository.findByAppointmentDateBetweenAndLbzDoctor(timeBetweenAppointmnets
                ,schedMedExamRequest.getAppointmentDate(), schedMedExamRequest.getLbzDoctor()))
                .thenReturn(Optional.of(new ArrayList<>()));

        mockConnectionWithUserService(1, HttpStatus.OK);

        assertThrows(BadRequestException.class, () -> schedMedExaminationService.createSchedMedExaminationLocked(schedMedExamRequest, token));

    }

    @Test
    public void createSchedMedExam_Success(){
        SchedMedExamRequest schedMedExamRequest= createSchedMedExamRequest();
        String token= "Bearer woauhruoawbhfupaw";

        Date timeBetweenAppointmnets = new Date(schedMedExamRequest.getAppointmentDate().getTime() - DURATION_OF_EXAM * 60 * 1000);

        Mockito.framework().clearInlineMocks();
        setUp();

        Patient patient=createPatient();
        when(scheduledMedExamRepository.findByAppointmentDateBetweenAndLbzDoctor(timeBetweenAppointmnets
                ,schedMedExamRequest.getAppointmentDate(), schedMedExamRequest.getLbzDoctor()))
                .thenReturn(Optional.of(new ArrayList<>()));

        when(patientRepository.findByLbpAndDeleted(schedMedExamRequest.getLbp(), false)).thenReturn(Optional.of(patient));

        ScheduledMedExamination scheduledMedExamination= schedMedExamMapper
                .schedMedExamRequestToScheduledMedExamination(new ScheduledMedExamination(),schedMedExamRequest, patient);

        when(scheduledMedExamRepository.save(any())).thenReturn(scheduledMedExamination);

        mockConnectionWithUserService(1, HttpStatus.OK);

        assertEquals(schedMedExaminationService.createSchedMedExaminationLocked(schedMedExamRequest, token)
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
        scheduledMedExamination.setPatient(createPatient());

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
        scheduledMedExamination.setPatient(createPatient());

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
        when(patientRepository.findByLbpAndDeleted(any(UUID.class), any(Boolean.class))).thenReturn(Optional.of(new Patient()));

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
        scheduledMedExamination.setPatient(createPatient());

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
        schedMedExamRequest.setAppointmentDate(new Date(System.currentTimeMillis() + 1000000L));
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

    private Patient createPatient(){
        Patient patient = new Patient();
        patient.setJmbg("1342002345612");
        patient.setFirstName("Pacijent");
        patient.setLastName("Pacijentovic");
        patient.setParentName("Roditelj");
        patient.setGender(Gender.MUSKI);
        patient.setBirthDate(new Date());
        patient.setBirthplace("Resnjak");
        patient.setCitizenshipCountry(CountryCode.SRB);
        patient.setCountryOfLiving(CountryCode.AFG);
        patient.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));

        patient.setAddress("Jurija Gagarina 16");
        patient.setPlaceOfLiving("Novi Beograd");
        patient.setPhoneNumber("0601234567");
        patient.setEmail("pacijent.pacijentovic@gmail.com");
        patient.setCustodianJmbg("0101987123456");
        patient.setCustodianName("Staratelj Starateljovic");
        patient.setFamilyStatus(FamilyStatus.OBA_RODITELJA);
        patient.setMaritalStatus(MaritalStatus.SAMAC);
        patient.setChildrenNum(0);
        patient.setEducation(Education.VISOKO_OBRAZOVANJE);
        patient.setProfession("Programer");

        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setRegistrationDate(new Date());
        healthRecord.setBloodType(BloodType.A);
        healthRecord.setRhFactor(RHFactor.PLUS);

        patient.setHealthRecord(healthRecord);

        return patient;
    }
}