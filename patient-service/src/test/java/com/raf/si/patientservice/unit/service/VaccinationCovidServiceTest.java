package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.request.VaccinationCovidRequest;
import com.raf.si.patientservice.dto.response.DosageReceivedResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.VaccinationMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.model.enums.testing.Availability;
import com.raf.si.patientservice.repository.AvailableTermRepository;
import com.raf.si.patientservice.repository.ScheduledVaccinationCovidRepository;
import com.raf.si.patientservice.repository.VaccinationCovidRepository;
import com.raf.si.patientservice.repository.VaccineRepository;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.VaccinationCovidService;
import com.raf.si.patientservice.service.impl.VaccinationCovidServiceImpl;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VaccinationCovidServiceTest {
    private ScheduledVaccinationCovidRepository scheduledVaccinationCovidRepository;
    private VaccinationCovidRepository vaccinationCovidRepository;
    private AvailableTermRepository availableTermRepository;
    private PatientService patientService;
    private VaccinationMapper vaccinationMapper;
    private VaccineRepository vaccineRepository;
    private Lock lock;
    private JdbcLockRegistry lockRegistry;
    private VaccinationCovidService vaccinationCovidService;

    @BeforeEach
    public void setUp(){
        scheduledVaccinationCovidRepository= mock(ScheduledVaccinationCovidRepository.class);
        vaccinationCovidRepository = mock(VaccinationCovidRepository.class);
        availableTermRepository = mock(AvailableTermRepository.class);
        patientService = mock(PatientService.class);
        vaccinationMapper = new VaccinationMapper();
        lock = mock(Lock.class);
        lockRegistry = mock(JdbcLockRegistry.class);
        vaccineRepository = mock(VaccineRepository.class);

        vaccinationCovidService= new VaccinationCovidServiceImpl(
                  vaccinationCovidRepository
                , scheduledVaccinationCovidRepository
                , availableTermRepository
                , patientService
                , vaccinationMapper
                , lockRegistry
                , vaccineRepository);

        when(patientService.findPatient((UUID) any()))
                .thenReturn(makePatient());

        mockTokenPayloadUtil();

    }

    @AfterEach
    public void cleanup() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    void scheduleVaccination_TwoUsersTryToPutTheLockAtTheSameTimeAndOneTimesOut_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        ScheduledVaccinationRequest request = makeScheduledVaccinationCovidRequest();
        String token= "Bearer woauhruoawbhfupaw";

        when(lockRegistry.obtain(any())).thenReturn(lock);
        try {
            when(lock.tryLock(1, TimeUnit.SECONDS)).thenReturn(false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertThrows(BadRequestException.class,
                () -> vaccinationCovidService.scheduleVaccination(lbp, request, token));
    }

    @Test
    void scheduleVaccination_LockingProducesInteruptedException_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        ScheduledVaccinationRequest request = makeScheduledVaccinationCovidRequest();
        String token= "Bearer woauhruoawbhfupaw";

        when(lockRegistry.obtain(any())).thenReturn(lock);
        try {
            when(lock.tryLock(1, TimeUnit.SECONDS)).thenThrow(new InterruptedException());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertThrows(InternalServerErrorException.class,
                () -> vaccinationCovidService.scheduleVaccination(lbp, request, token));
    }

    @Test
    void scheduledVaccination_GivenDateIsInThePast_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        ScheduledVaccinationRequest request = makeScheduledVaccinationCovidRequest();
        request.setDateAndTime(LocalDateTime.now().minusDays(2));
        String token= "Bearer woauhruoawbhfupaw";

        lokin();

        assertThrows(BadRequestException.class,
                () -> vaccinationCovidService.scheduleVaccination(lbp, request, token));
    }

    @Test
    void scheduledVaccination_PatientHadScheduledTestInThePast2Days_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        ScheduledVaccinationRequest request = makeScheduledVaccinationCovidRequest();
        String token= "Bearer woauhruoawbhfupaw";

        lokin();

        when(scheduledVaccinationCovidRepository.findByPatientAndDateAndTimeBetween(any(), any(), any()))
                .thenReturn(List.of(makeSchedVaccCovid()));

        assertThrows(BadRequestException.class,
                () -> vaccinationCovidService.scheduleVaccination(lbp, request, token));
    }

    @Test
    void scheduledVaccination_ThereAreMultipleAppointmentsScheduledForTheRequestedTime_ThrowInternalServerErrorException(){
        UUID lbp = UUID.randomUUID();
        ScheduledVaccinationRequest request = makeScheduledVaccinationCovidRequest();
        String token= "Bearer woauhruoawbhfupaw";

        lokin();
        AvailableTerm availableTerm = makeAvailableTerm();

        when(availableTermRepository.findByDateAndTimeBetweenAndPbo(
                any(),
                any(),
                any()
        )).thenReturn(Arrays.asList(availableTerm, availableTerm));

        assertThrows(InternalServerErrorException.class,
                () -> vaccinationCovidService.scheduleVaccination(lbp, request, token));
    }

    @Test
    void scheduledVaccination_AppointmentFullyBooked_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        ScheduledVaccinationRequest request = makeScheduledVaccinationCovidRequest();
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
                () -> vaccinationCovidService.scheduleVaccination(lbp, request, token));
    }

    @Test
    void scheduledVaccination_Success(){
        UUID lbp = UUID.randomUUID();
        ScheduledVaccinationRequest request = makeScheduledVaccinationCovidRequest();
        String token= "Bearer woauhruoawbhfupaw";
        ScheduledVaccinationCovid vaccCovid = makeSchedVaccCovid();

        lokin();
        AvailableTerm availableTerm = makeAvailableTerm();

        when(availableTermRepository.findByDateAndTimeBetweenAndPbo(
                any(),
                any(),
                any()
        )).thenReturn(List.of(availableTerm));

        when(availableTermRepository.save(any())).thenReturn(makeAvailableTerm());
        when(scheduledVaccinationCovidRepository.save(any())).thenReturn(vaccCovid);

        assertEquals(vaccinationMapper.scheduledVaccinationToResponse(vaccCovid)
                , vaccinationCovidService.scheduleVaccination(lbp, request, token));
    }

    @Test
    void getScheduledVaccination(){
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        Page<ScheduledVaccinationCovid> page= new PageImpl<>(List.of(makeSchedVaccCovid()),pageable, 1);

        when(scheduledVaccinationCovidRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        assertEquals(vaccinationMapper.scheduledVaccinationPageToResponse(page)
                , vaccinationCovidService.getScheduledVaccinations(null, null, pageable));

    }

    @Test
    void createVaccination_GivenVaccineNotFound_ThrowBadRequestException(){
        UUID lbp = UUID.randomUUID();
        VaccinationCovidRequest request = makeVaccinationCovidRequest();
        String token = "bopewqimqpoqeww";

        assertThrows(BadRequestException.class,
                () -> vaccinationCovidService.createVaccination(UUID.randomUUID(), request, token));
    }

    @Test
    void createVaccination_DateIsInFuture_ThrowBadRequestException(){
        VaccinationCovidRequest request = makeVaccinationCovidRequest();
        String token = "bopewqimqpoqeww";
        request.setDateTime(LocalDateTime.now().plusDays(1));

        when(vaccineRepository.findByName(request.getVaccineName()))
                .thenReturn(Optional.of(makeVaccine()));

        assertThrows(BadRequestException.class,
                () -> vaccinationCovidService.createVaccination(UUID.randomUUID(), request, token));
    }

    @Test
    void createVaccination_GivenSVCIdDoesNotExits_ThrowBadRequestException(){
        VaccinationCovidRequest request = makeVaccinationCovidRequest();
        String token = "bopewqimqpoqeww";

        when(vaccineRepository.findByName(request.getVaccineName()))
                .thenReturn(Optional.of(makeVaccine()));

        assertThrows(BadRequestException.class,
                () -> vaccinationCovidService.createVaccination(UUID.randomUUID(), request, token));
    }

    @Test
    void createVaccination_Success(){
        VaccinationCovidRequest request = makeVaccinationCovidRequest();
        String token = "bopewqimqpoqeww";
        VaccinationCovid vaccinationCovid = makeVaccinationCovid();
        ScheduledVaccinationCovid scheduledVaccinationCovid = makeSchedVaccCovid();
        
        vaccinationCovid.setScheduledVaccinationCovid(scheduledVaccinationCovid);

        when(vaccineRepository.findByName(request.getVaccineName()))
                .thenReturn(Optional.of(makeVaccine()));
        when(scheduledVaccinationCovidRepository.findById(request.getVaccinationId()))
                .thenReturn(Optional.of(scheduledVaccinationCovid));

        when(vaccinationCovidRepository.save(any())).thenReturn(vaccinationCovid);
        when(scheduledVaccinationCovidRepository.save(any())).thenReturn(makeSchedVaccCovid());

        assertEquals(vaccinationMapper.vaccinationCovidToResponse(vaccinationCovid)
                , vaccinationCovidService.createVaccination(UUID.randomUUID(), request, token));
    }

    @Test
    void getPatientDosageReceived_VaccineRecordDoesNotExits_Success(){
        UUID lbp = UUID.randomUUID();
        DosageReceivedResponse response = new DosageReceivedResponse(0L);

        assertEquals(response
                , vaccinationCovidService.getPatientDosageReceived(lbp));
    }

    @Test
    void getPatientDosageReceived_Success(){
        UUID lbp = UUID.randomUUID();
        DosageReceivedResponse response = new DosageReceivedResponse(1L);

        when(vaccinationCovidRepository.findByHealthRecord_Patient(any()))
                .thenReturn(List.of(makeVaccinationCovid()));

        assertEquals(response
                , vaccinationCovidService.getPatientDosageReceived(lbp));
    }

    @Test
    void changeScheduledVaccinationStatus_GivenAllNull_ThrowBadRequestException(){
        assertThrows(BadRequestException.class,
                () -> vaccinationCovidService.changeScheduledVaccinationStatus(1L, null, null));
    }

    @Test
    void changeScheduledVaccinationStatus_Success(){
        ScheduledVaccinationCovid schedVaccCovid = makeSchedVaccCovid();

        when(scheduledVaccinationCovidRepository.findById(any())).thenReturn(Optional.of(schedVaccCovid));
        when(scheduledVaccinationCovidRepository.save(schedVaccCovid)).thenReturn(schedVaccCovid);

        schedVaccCovid.setTestStatus(ExaminationStatus.ZAKAZANO);
        schedVaccCovid.setPatientArrivalStatus(PatientArrivalStatus.PRIMLJEN);
        assertEquals(vaccinationMapper.scheduledVaccinationToResponse(schedVaccCovid)
                , vaccinationCovidService.changeScheduledVaccinationStatus(1L, "Zakazano", "Primljen"));
    }


    private VaccinationCovid makeVaccinationCovid() {
        VaccinationCovid vaccinationCovid = new VaccinationCovid();
        vaccinationCovid.setVaccine(makeVaccine());
        vaccinationCovid.setScheduledVaccinationCovid(makeSchedVaccCovid());
        vaccinationCovid.setPerformerLbz(UUID.randomUUID());
        vaccinationCovid.setHealthRecord(makeHealthRecord());
        vaccinationCovid.getHealthRecord().setId(0L);
        vaccinationCovid.setDateTime(LocalDateTime.now().minusDays(1));
        vaccinationCovid.setDoseReceived(1L);

        return  vaccinationCovid;
    }


    private Vaccine makeVaccine(){
        Vaccine vaccine = new Vaccine();
        vaccine.setName("PRIORIX");
        vaccine.setId(1L);
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");
        return vaccine;
    }

    private VaccinationCovidRequest makeVaccinationCovidRequest(){
        VaccinationCovidRequest request = new VaccinationCovidRequest();
        request.setVaccinationId(8L);
        request.setVaccineName("SARS");
        request.setDateTime(LocalDateTime.now());
        request.setDoseReceived(1L);

        return request;
    }

    private void lokin(){
        when(lockRegistry.obtain(any())).thenReturn(lock);
        try {
            when(lock.tryLock(1, TimeUnit.SECONDS)).thenReturn(true);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private ScheduledVaccinationRequest makeScheduledVaccinationCovidRequest() {
        ScheduledVaccinationRequest scheduledVaccinationRequest = new ScheduledVaccinationRequest();
        scheduledVaccinationRequest.setDateAndTime(LocalDateTime.now().plusDays(1));
        return  scheduledVaccinationRequest;
    }


    private ScheduledVaccinationCovid makeSchedVaccCovid() {
        ScheduledVaccinationCovid vaccinationCovid= new ScheduledVaccinationCovid();

        vaccinationCovid.setDateAndTime(LocalDateTime.now());
        vaccinationCovid.setNote("");
        vaccinationCovid.setSchedulerLbz(UUID.randomUUID());
        vaccinationCovid.setPatientArrivalStatus(PatientArrivalStatus.CEKA);
        vaccinationCovid.setTestStatus(ExaminationStatus.ZAKAZANO);
        vaccinationCovid.setPatient(makePatient());
        vaccinationCovid.setAvailableTerm(makeAvailableTerm());
        return vaccinationCovid;
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
        patient.setHealthRecord(makeHealthRecord());
        return patient;
    }

    private HealthRecord makeHealthRecord(){
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setId(0L);

        healthRecord.setBloodType(BloodType.A);
        healthRecord.setRhFactor(RHFactor.PLUS);
        healthRecord.setRegistrationDate(new Date());

        Allergy allergy = new Allergy();
        Operation operation = new Operation();



        //createExaminationReportRequest.setDiagnosis("I35.0");
        //createExaminationReportRequest.setExistingDiagnosis(Boolean.FALSE);
        //createExaminationReportRequest.setTreatmentResult("U toku");
        //createExaminationReportRequest.setCurrentStateDescription("mora da ima ovo polje");

        MedicalExamination examination = new MedicalExamination();
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("I35.0");
        diagnosis.setLatinDescription("latino dasa");
        diagnosis.setDescription("ujed latino komarca");

        examination.setObjectiveFinding("objektivan nalaz");
        examination.setHealthRecord(healthRecord);
        examination.setMainSymptoms("nema simptoma");
        examination.setCurrentIllness("nema boljki");
        examination.setDiagnosis(diagnosis);

        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setDiagnosis(diagnosis);
        medicalHistory.setValidFrom(new Date());
        medicalHistory.setHealthRecord(healthRecord);
        medicalHistory.setIllnessStart(new Date());

        Vaccination vaccination = new Vaccination();

        List<Allergy> allergies = new ArrayList<>();
        allergies.add(allergy);
        List<Operation> operations = Arrays.asList(new Operation[] {operation});
        List<MedicalHistory> medicalHistoryList = new ArrayList<>();
        medicalHistoryList.add(medicalHistory);
        List<MedicalExamination> examinations = new ArrayList<>();
        examinations.add(examination);
        List<Vaccination> vaccinations = new ArrayList<>();
        vaccinations.add(vaccination);

        healthRecord.setAllergies(allergies);
        healthRecord.setOperations(operations);
        healthRecord.setMedicalExaminations(examinations);
        healthRecord.setMedicalHistory(medicalHistoryList);
        healthRecord.setVaccinations(vaccinations);

        return healthRecord;
    }
}
