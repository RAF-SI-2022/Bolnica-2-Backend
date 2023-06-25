package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.request.TestingRequest;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.exception.NotFoundException;
import com.raf.si.patientservice.mapper.HospitalizationMapper;
import com.raf.si.patientservice.mapper.TestingMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
import com.raf.si.patientservice.model.enums.testing.Availability;
import com.raf.si.patientservice.repository.AvailableTermRepository;
import com.raf.si.patientservice.repository.PatientConditionRepository;
import com.raf.si.patientservice.repository.ScheduledTestingRepository;
import com.raf.si.patientservice.repository.TestingRepository;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.TestingService;
import com.raf.si.patientservice.service.impl.TestingServiceImpl;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestingServiceTest {

    private  ScheduledTestingRepository scheduledTestingRepository;
    private  TestingRepository testingRepository;
    private  AvailableTermRepository availableTermRepository;
    private  PatientConditionRepository patientConditionRepository;
    private  PatientService patientService;
    private  TestingMapper testingMapper;
    private  JdbcLockRegistry lockRegistry;
    private Lock lock;
    private TestingService testingService;

    @BeforeEach
    public void setUp(){
        scheduledTestingRepository = mock(ScheduledTestingRepository.class);
        testingRepository = mock(TestingRepository.class);
        availableTermRepository = mock(AvailableTermRepository.class);
        patientConditionRepository = mock(PatientConditionRepository.class);
        testingMapper = new TestingMapper(new HospitalizationMapper());
        lockRegistry = mock(JdbcLockRegistry.class);
        lock = mock(Lock.class);
        patientService = mock(PatientService.class);

        testingService = new TestingServiceImpl(
                  scheduledTestingRepository
                , testingRepository
                , availableTermRepository
                , patientConditionRepository
                , patientService
                , testingMapper
                , lockRegistry);

        when(patientService.findPatient((UUID) any()))
                .thenReturn(makePatient());

        mockTokenPayloadUtil();

    }


    @AfterEach
    public void cleanup() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    void scheduleTesting_TwoUsersTryToPutTheLockAtTheSameTimeAndOneTimesOut_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        ScheduledTestingRequest request = makeScheduledTestingRequest();
        String token= "Bearer woauhruoawbhfupaw";

        when(lockRegistry.obtain(any())).thenReturn(lock);
        try {
            when(lock.tryLock(1, TimeUnit.SECONDS)).thenReturn(false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertThrows(BadRequestException.class,
                () -> testingService.scheduleTesting(lbp, request, token));
    }

    @Test
    void scheduleTesting_LockingProducesInteruptedException_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        ScheduledTestingRequest request = makeScheduledTestingRequest();
        String token= "Bearer woauhruoawbhfupaw";

        when(lockRegistry.obtain(any())).thenReturn(lock);
        try {
            when(lock.tryLock(1, TimeUnit.SECONDS)).thenThrow(new InterruptedException());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertThrows(InternalServerErrorException.class,
                () -> testingService.scheduleTesting(lbp, request, token));
    }

    @Test
    void scheduledTesting_GivenDateIsInThePast_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        ScheduledTestingRequest request = makeScheduledTestingRequest();
        request.setDateAndTime(LocalDateTime.now().minusDays(2));
        String token= "Bearer woauhruoawbhfupaw";

        lokin();

        assertThrows(BadRequestException.class,
                () -> testingService.scheduleTesting(lbp, request, token));
    }

    @Test
    void scheduledTesting_PatientHadScheduledTestInThePast2Days_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        ScheduledTestingRequest request = makeScheduledTestingRequest();
        String token= "Bearer woauhruoawbhfupaw";

        lokin();

        when(scheduledTestingRepository.findByPatientAndDateAndTimeBetween(any(), any(), any()))
                .thenReturn(List.of(makeSchedTest()));

        assertThrows(BadRequestException.class,
                () -> testingService.scheduleTesting(lbp, request, token));
    }

    @Test
    void scheduledTesting_ThereAreMultipleAppointmentsScheduledForTheRequestedTime_ThrowInternalServerErrorException(){
        UUID lbp = UUID.randomUUID();
        ScheduledTestingRequest request = makeScheduledTestingRequest();
        String token= "Bearer woauhruoawbhfupaw";

        lokin();
        AvailableTerm availableTerm = makeAvailableTerm();

        when(availableTermRepository.findByDateAndTimeBetweenAndPbo(
                        any(),
                        any(),
                        any()
                        )).thenReturn(Arrays.asList(availableTerm, availableTerm));

        assertThrows(InternalServerErrorException.class,
                () -> testingService.scheduleTesting(lbp, request, token));
    }

    @Test
    void scheduledTesting_AppointmentFullyBooked_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        ScheduledTestingRequest request = makeScheduledTestingRequest();
        String token= "Bearer woauhruoawbhfupaw";

        lokin();
        AvailableTerm availableTerm = makeAvailableTerm();
        availableTerm.setAvailability(Availability.POTPUNO_POPUNJEN_TERMIN);

        when(availableTermRepository.findByDateAndTimeBetweenAndPbo(
                any(),
                any(),
                any()
        )).thenReturn(List.of(availableTerm));

        assertThrows(BadRequestException.class,
                () -> testingService.scheduleTesting(lbp, request, token));
    }

    @Test
    void scheduledTesting_Success(){
        UUID lbp = UUID.randomUUID();
        ScheduledTestingRequest request = makeScheduledTestingRequest();
        String token= "Bearer woauhruoawbhfupaw";
        ScheduledTesting scheduledTesting = makeSchedTest();

        lokin();
        AvailableTerm availableTerm = makeAvailableTerm();

        when(availableTermRepository.findByDateAndTimeBetweenAndPbo(
                any(),
                any(),
                any()
        )).thenReturn(List.of(availableTerm));

        when(availableTermRepository.save(any())).thenReturn(makeAvailableTerm());
        when(scheduledTestingRepository.save(any())).thenReturn(scheduledTesting);

        assertEquals(testingMapper.scheduledTestingToResponse(scheduledTesting)
                , testingService.scheduleTesting(lbp, request, token));
    }

    @Test
    void getAvailableTerm_Success(){
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        String token= "Bearer zimaJeTrcimNapoljuJeHladno";
        AvailableTerm availableTerm= makeAvailableTerm();

        when(availableTermRepository.findByDateAndTimeAndPbo(any(), any()))
                .thenReturn(Optional.of(availableTerm));

        assertEquals(testingMapper.availableTermToResponse(availableTerm)
                , testingService.getAvailableTerm(date, token));
    }

    @Test
    void getScheduledtestings(){
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        Page<ScheduledTesting> page= new PageImpl<>(List.of(makeSchedTest()),pageable, 1);

        when(scheduledTestingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        assertEquals(testingMapper.scheduledTestingPageToResponse(page)
                , testingService.getScheduledtestings(null, null, pageable));

    }

    @Test
    void createTesting_GivenIdNotFound_ThrowNotFoundException(){
        TestingRequest request = makeTestingRequest();

        assertThrows(NotFoundException.class,
                () -> testingService.createTesting(UUID.randomUUID(),request));
    }

    @Test
    void createTesting_CollectDateIsInFuture_ThrowBadRequestException(){
        TestingRequest request = makeTestingRequest();
        request.setCollectedInfoDate(Date.from(LocalDate.now().plusDays(3)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        request.setScheduledTestingId(null);
        ScheduledTesting scheduledTesting = makeSchedTest();

        when(scheduledTestingRepository.save(any())).thenReturn(scheduledTesting);

        assertThrows(BadRequestException.class,
                () -> testingService.createTesting(UUID.randomUUID(),request));
    }

    @Test
    void createTesting_Success(){
        ScheduledTesting scheduledTesting = makeSchedTest();
        Testing testing= makeTesting();
        TestingRequest request = makeTestingRequest();
        request.setScheduledTestingId(null);

        when(testingRepository.save(any())).thenReturn(testing);
        when(scheduledTestingRepository.save(any())).thenReturn(scheduledTesting);
        when(patientConditionRepository.save(any())).thenReturn(testing.getPatientCondition());

        assertEquals(testingMapper.testingToResponse(testing)
                , testingService.createTesting(UUID.randomUUID(), request));
    }

    @Test
    void changeScheduledTestingStatus_GivenAllNull_ThrowBadRequestException(){
        assertThrows(BadRequestException.class,
                () -> testingService.changeScheduledTestingStatus(1L, null, null));
    }

    @Test
    void changeScheduledTestingStatus_Success(){
        ScheduledTesting scheduledTesting = makeSchedTest();

        when(scheduledTestingRepository.findById(any())).thenReturn(Optional.of(scheduledTesting));
        when(scheduledTestingRepository.save(scheduledTesting)).thenReturn(scheduledTesting);

        scheduledTesting.setTestStatus(ExaminationStatus.ZAKAZANO);
        scheduledTesting.setPatientArrivalStatus(PatientArrivalStatus.PRIMLJEN);
        assertEquals(testingMapper.scheduledTestingToResponse(scheduledTesting)
                , testingService.changeScheduledTestingStatus(1L, "Zakazano", "Primljen"));
    }


    @Test
    void deleteScheduledTesting_Success(){
        ScheduledTesting scheduledTesting = makeSchedTest();
        scheduledTesting.getAvailableTerm().addScheduledTesting(scheduledTesting);

        when(scheduledTestingRepository.findById(any())).thenReturn(Optional.of(scheduledTesting));

        assertEquals(testingMapper.scheduledTestingToResponse(scheduledTesting)
                , testingService.deleteScheduledTesting(1L));
    }



    private Testing makeTesting(){
        Testing testing = new Testing();
        testing.setPatient(makePatient());
        testing.setReason("Heart To Heart- By Aaron");
        testing.setPatientCondition(makePatientCondition());
        return  testing;
    }

    private PatientCondition makePatientCondition(){
        PatientCondition patientCondition= new PatientCondition();
        patientCondition.setPatient(makePatient());
        patientCondition.setOnRespirator(false);
        patientCondition.setDescription("Belong Together By Yellow Days");
        patientCondition.setPulse("Time Glitch by Duster");
        patientCondition.setBloodPressure("Part 3 by crumb");
        patientCondition.setTemperature("Slow party by WILLIS");
        patientCondition.setAppliedTherapies("Feels Like Summer by Childish Gambino");
        patientCondition.setCollectedInfoDate(Date.from(LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        patientCondition.setRegisterLbz(UUID.randomUUID());
        return patientCondition;
    }


    private TestingRequest makeTestingRequest() {
        TestingRequest testingRequest= new TestingRequest();
        testingRequest.setAppliedTherapies("");
        testingRequest.setScheduledTestingId(32L);
        testingRequest.setReason("403");
        testingRequest.setDescription("Imagine Dragons");
        testingRequest.setPulse("200");
        testingRequest.setTemperature("49");
        testingRequest.setBloodPressure("9949");
        testingRequest.setCollectedInfoDate(Date.from(LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return testingRequest;
    }


    private void lokin(){
        when(lockRegistry.obtain(any())).thenReturn(lock);
        try {
            when(lock.tryLock(1, TimeUnit.SECONDS)).thenReturn(true);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private AvailableTerm makeAvailableTerm(){
        AvailableTerm availableTerm = new AvailableTerm();
        availableTerm.setAvailability(Availability.MOGUCE_ZAKAZATI_U_OVOM_TERMINU);
        availableTerm.setPbo(UUID.randomUUID());
        availableTerm.setAvailableNursesNum(3);
        availableTerm.setScheduledTermsNum(2);
        availableTerm.setDateAndTime(LocalDateTime.now().plusDays(1));
        availableTerm.setScheduledTestings(new ArrayList<>());
        return availableTerm;
    }


    private ScheduledTesting makeSchedTest() {
        ScheduledTesting scheduledTesting= new ScheduledTesting();

        scheduledTesting.setDateAndTime(LocalDateTime.now());
        scheduledTesting.setNote("");
        scheduledTesting.setSchedulerLbz(UUID.randomUUID());
        scheduledTesting.setPatientArrivalStatus(PatientArrivalStatus.CEKA);
        scheduledTesting.setTestStatus(ExaminationStatus.ZAKAZANO);
        scheduledTesting.setPatient(makePatient());
        scheduledTesting.setAvailableTerm(makeAvailableTerm());
        return scheduledTesting;
    }

    private ScheduledTestingRequest makeScheduledTestingRequest() {
        ScheduledTestingRequest scheduledTestingRequest = new ScheduledTestingRequest();
        scheduledTestingRequest.setDateAndTime(LocalDateTime.now().plusDays(1));
        return  scheduledTestingRequest;
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
        tokenPayload.setPermissions(List.of("ROLE_MED_SESTRA"));

        return tokenPayload;
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
}
