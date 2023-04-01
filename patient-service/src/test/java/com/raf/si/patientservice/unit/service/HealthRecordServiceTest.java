package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.MedicalExaminationFilterRequest;
import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.LightHealthRecordResponse;
import com.raf.si.patientservice.mapper.HealthRecordMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.repository.*;
import com.raf.si.patientservice.service.HealthRecordService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.impl.HealthRecordServiceImpl;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public class HealthRecordServiceTest {

    private HealthRecordService healthRecordService;
    private AllergyRepository allergyRepository;
    private VaccinationRepository vaccinationRepository;
    private MedicalExaminationRepository medicalExaminationRepository;
    private MedicalHistoryRepository medicalHistoryRepository;
    private OperationRepository operationRepository;
    private PatientService patientService;
    private HealthRecordMapper healthRecordMapper;

    @BeforeEach
    public void setUp(){
        allergyRepository = mock(AllergyRepository.class);
        vaccinationRepository = mock(VaccinationRepository.class);
        medicalExaminationRepository = mock(MedicalExaminationRepository.class);
        medicalHistoryRepository = mock(MedicalHistoryRepository.class);
        operationRepository = mock(OperationRepository.class);
        patientService = mock(PatientService.class);
        healthRecordMapper = new HealthRecordMapper();

        healthRecordService = new HealthRecordServiceImpl(allergyRepository,
                vaccinationRepository,
                medicalExaminationRepository,
                medicalHistoryRepository,
                operationRepository,
                patientService,
                healthRecordMapper);
    }

    @Test
    public void getHealthRecordForPatientTest_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Pageable pageable = PageRequest.of(0, 5);

        when(patientService.findPatient((UUID) any())).thenReturn(patient);
        when(allergyRepository.findByHealthRecord(any(), any()))
                .thenReturn(new PageImpl<>(healthRecord.getAllergies()));
        when(operationRepository.findByHealthRecord(any(), any()))
                .thenReturn(new PageImpl<>(healthRecord.getOperations()));
        when(vaccinationRepository.findByHealthRecord(any(), any()))
                .thenReturn(new PageImpl<>(healthRecord.getVaccinations()));
        when(medicalHistoryRepository.findAll((Specification<MedicalHistory>) any(), (Pageable) any()))
                .thenReturn(new PageImpl<>(healthRecord.getMedicalHistory()));
        when(medicalExaminationRepository.findAll((Specification<MedicalExamination>) any(), (Pageable) any()))
                .thenReturn(new PageImpl<>(healthRecord.getMedicalExaminations()));

        HealthRecordResponse response = healthRecordMapper.healthRecordToHealthRecordResponse(
                patient,
                healthRecord,
                new PageImpl<>(healthRecord.getAllergies()),
                new PageImpl<>(healthRecord.getVaccinations()),
                new PageImpl<>(healthRecord.getMedicalExaminations()),
                new PageImpl<>(healthRecord.getMedicalHistory()),
                new PageImpl<>(healthRecord.getOperations())
        );

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayload());

            assertEquals(healthRecordService.getHealthRecordForPatient(patient.getLbp(), pageable),
                    response);
        }
    }

    @Test
    public void getLightHealthRecordForPatientTest_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Pageable pageable = PageRequest.of(0, 5);

        when(patientService.findPatient((UUID) any())).thenReturn(patient);
        when(allergyRepository.findByHealthRecord(any(), any()))
                .thenReturn(new PageImpl<>(healthRecord.getAllergies()));
        when(vaccinationRepository.findByHealthRecord(any(), any()))
                .thenReturn(new PageImpl<>(healthRecord.getVaccinations()));

        LightHealthRecordResponse response = healthRecordMapper.healthRecordToLightHealthRecordResponse(
                patient,
                healthRecord,
                new PageImpl<>(healthRecord.getAllergies()),
                new PageImpl<>(healthRecord.getVaccinations())
        );

        assertEquals(healthRecordService.getLightHealthRecordForPatient(patient.getLbp(), pageable),
                response);
    }

    @Test
    public void findExaminationTest_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Pageable pageable = PageRequest.of(0, 5);

        MedicalExaminationFilterRequest request = new MedicalExaminationFilterRequest();
        request.setStartDate(new Date());
        request.setEndDate(new Date());

        when(patientService.findPatient((UUID) any())).thenReturn(patient);
        when(medicalExaminationRepository.findAll((Specification<MedicalExamination>) any(), (Pageable) any()))
                .thenReturn(new PageImpl<>(healthRecord.getMedicalExaminations()));

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayload());

            assertEquals(healthRecordService.findExaminations(patient.getLbp(), request, pageable),
                    healthRecordMapper.getPermittedExaminations(new PageImpl<>(healthRecord.getMedicalExaminations())));
        }
    }

    @Test
    public void findExaminationTest_Confidential_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Pageable pageable = PageRequest.of(0, 5);

        MedicalExaminationFilterRequest request = new MedicalExaminationFilterRequest();
        request.setStartDate(new Date());
        request.setEndDate(new Date());

        when(patientService.findPatient((UUID) any())).thenReturn(patient);
        when(medicalExaminationRepository.findAll((Specification<MedicalExamination>) any(), (Pageable) any()))
                .thenReturn(new PageImpl<>(healthRecord.getMedicalExaminations()));

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayloadConf());

            assertEquals(healthRecordService.findExaminations(patient.getLbp(), request, pageable),
                    healthRecordMapper.getPermittedExaminations(new PageImpl<>(healthRecord.getMedicalExaminations())));
        }
    }

    @Test
    public void findHistoryTest_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        String diagnosis = "diagnosis";
        Pageable pageable = PageRequest.of(0, 5);

        when(patientService.findPatient((UUID) any())).thenReturn(patient);
        when(medicalHistoryRepository.findAll((Specification<MedicalHistory>) any(), (Pageable) any()))
                .thenReturn(new PageImpl<>(healthRecord.getMedicalHistory()));

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayload());

            assertEquals(healthRecordService.findMedicalHistory(patient.getLbp(), diagnosis, pageable),
                    healthRecordMapper.getPermittedMedicalHistory(new PageImpl<>(healthRecord.getMedicalHistory())));
        }
    }

    @Test
    public void findHistoryTest_Confidential_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        String diagnosis = "diagnosis";
        Pageable pageable = PageRequest.of(0, 5);

        when(patientService.findPatient((UUID) any())).thenReturn(patient);
        when(medicalHistoryRepository.findAll((Specification<MedicalHistory>) any(), (Pageable) any()))
                .thenReturn(new PageImpl<>(healthRecord.getMedicalHistory()));

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayloadConf());

            assertEquals(healthRecordService.findMedicalHistory(patient.getLbp(), diagnosis, pageable),
                    healthRecordMapper.getPermittedMedicalHistory(new PageImpl<>(healthRecord.getMedicalHistory())));
        }
    }

    @Test
    public void findExaminationsTest_EndDateNull_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Pageable pageable = PageRequest.of(0, 5);

        MedicalExaminationFilterRequest request = new MedicalExaminationFilterRequest();
        request.setStartDate(new Date());

        when(patientService.findPatient((UUID) any())).thenReturn(patient);
        when(medicalExaminationRepository.findAll((Specification<MedicalExamination>) any(), (Pageable) any()))
                .thenReturn(new PageImpl<>(healthRecord.getMedicalExaminations()));

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayload());

            assertEquals(healthRecordService.findExaminations(patient.getLbp(), request, pageable),
                    healthRecordMapper.getPermittedExaminations(new PageImpl<>(healthRecord.getMedicalExaminations())));
        }
    }




    private Patient makePatient(){
        HealthRecord healthRecord = makeHealthRecord();
        Patient patient = new Patient();
        patient.setHealthRecord(healthRecord);
        return patient;
    }

    private HealthRecord makeHealthRecord(){
        HealthRecord healthRecord = new HealthRecord();

        healthRecord.setBloodType(BloodType.A);
        healthRecord.setRhFactor(RHFactor.PLUS);
        healthRecord.setRegistrationDate(new Date());

        Allergy allergy = new Allergy();
        Operation operation = new Operation();
        MedicalHistory history = new MedicalHistory();
        MedicalExamination examination = new MedicalExamination();
        Vaccination vaccination = new Vaccination();

        List<Allergy> allergies = Arrays.asList(new Allergy[] {allergy});
        List<Operation> operations = Arrays.asList(new Operation[] {operation});
        List<MedicalHistory> medicalHistoryList = Arrays.asList(new MedicalHistory[] {history});
        List<MedicalExamination> examinations = Arrays.asList(new MedicalExamination[] {examination});
        List<Vaccination> vaccinations = Arrays.asList(new Vaccination[] {vaccination});

        healthRecord.setAllergies(allergies);
        healthRecord.setOperations(operations);
        healthRecord.setMedicalExaminations(examinations);
        healthRecord.setMedicalHistory(medicalHistoryList);
        healthRecord.setVaccinations(vaccinations);

        return healthRecord;
    }

    private TokenPayload makeTokenPayloadConf(){
        TokenPayload payload = new TokenPayload();
        payload.setPermissions(Arrays.asList(new String[] {"ROLE_DR_SPEC_POV"}));
        return payload;
    }

    private TokenPayload makeTokenPayload(){
        TokenPayload payload = new TokenPayload();
        payload.setPermissions(new ArrayList<>());
        return payload;
    }
}
