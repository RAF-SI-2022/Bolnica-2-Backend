package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.*;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private VaccineRepository vaccineRepository;
    private AllergenRepository allergenRepository;
    private DiagnosisRepository diagnosisRepository;
    private HealthRecordRepository healthRecordRepository;
    private PatientService patientService;
    private HealthRecordMapper healthRecordMapper;

    @BeforeEach
    public void setUp(){
        allergyRepository = mock(AllergyRepository.class);
        vaccinationRepository = mock(VaccinationRepository.class);
        medicalExaminationRepository = mock(MedicalExaminationRepository.class);
        medicalHistoryRepository = mock(MedicalHistoryRepository.class);
        operationRepository = mock(OperationRepository.class);
        vaccineRepository = mock(VaccineRepository.class);
        allergenRepository = mock(AllergenRepository.class);
        diagnosisRepository = mock(DiagnosisRepository.class);
        healthRecordRepository = mock(HealthRecordRepository.class);
        patientService = mock(PatientService.class);
        healthRecordMapper = new HealthRecordMapper();
        diagnosisRepository = mock(DiagnosisRepository.class);
        allergenRepository = mock(AllergenRepository.class);
        diagnosisRepository = mock(DiagnosisRepository.class);
        healthRecordRepository = mock(HealthRecordRepository.class);
        vaccineRepository = mock(VaccineRepository.class);


        healthRecordService = new HealthRecordServiceImpl(allergyRepository,
                diagnosisRepository,
                allergenRepository,
                vaccineRepository,
                vaccinationRepository,
                medicalExaminationRepository,
                medicalHistoryRepository,
                operationRepository,
                healthRecordRepository,
                patientService,
                healthRecordMapper);
    }

    @Test
    void getHealthRecordForPatientTest_Success(){
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
    void getLightHealthRecordForPatientTest_Success(){
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
    void findExaminationTest_Success(){
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
    void findExaminationTest_Confidential_Success(){
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
    void findHistoryTest_Success(){
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
    void findHistoryTest_Confidential_Success(){
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
    void findExaminationsTest_EndDateNull_Success(){
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

    @Test
    void getAvailableAllergens_Success(){
        Allergen allergen1 = makeAllergen();
        Allergen allergen2 = new Allergen();
        allergen2.setName("mleko");
        List<Allergen> allergens = new ArrayList<>();
        allergens.add(allergen1);
        allergens.add(allergen2);
        when(allergenRepository.findAll()).thenReturn(allergens);

        List<AllergenResponse> allergenResponses = new ArrayList<>();
        for(Allergen allergen : allergens) {
            allergenResponses.add(new AllergenResponse(allergen.getId(),allergen.getName()));
        }

        AllergenListResponse allergenListResponse = new AllergenListResponse(allergenResponses);


        assertEquals(healthRecordService.getAvailableAllergens(), allergenListResponse);

    }

    @Test
    void getAvailableVaccines_Success(){
        Vaccine vaccine1 = new Vaccine();
        vaccine1.setName("covid-19");
        vaccine1.setDescription("kinezi");
        vaccine1.setType("virusna");
        vaccine1.setProducer("jackie chan");

        Vaccine vaccine2 = new Vaccine();
        vaccine2.setName("covid-20");
        vaccine2.setDescription("amerikanci");
        vaccine2.setType("bakterijska");
        vaccine2.setProducer("novak djokovic");
        List<Vaccine> vaccines = new ArrayList<>();
        vaccines.add(vaccine1);
        vaccines.add(vaccine2);
        when(vaccineRepository.findAll()).thenReturn(vaccines);

        List<VaccineResponse> vaccineResponses = new ArrayList<>();
        for(Vaccine vaccine : vaccines) {
            vaccineResponses.add(new VaccineResponse(vaccine.getId(),vaccine.getName(), vaccine.getType(), vaccine.getDescription(), vaccine.getProducer()));
        }

        VaccineListResponse vaccineListResponse = new VaccineListResponse(vaccineResponses);


        assertEquals(healthRecordService.getAvailableVaccines(), vaccineListResponse);

    }


    @Test
    void addAllergy_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddAllergyRequest addAllergyRequest = new AddAllergyRequest();
        addAllergyRequest.setAllergen("mleko");
        Allergen allergen = new Allergen();
        allergen.setName("mleko");
        allergen.setId(1L);

        Allergy allergy = new Allergy();
        allergy.setId(1L);
        allergy.setHealthRecord(healthRecord);
        allergy.setAllergen(allergen);

        // kreiraj add alergy request
            //samo ime alergen-a potrebno
            // lbp

        // mock get allergen from database
        when(allergenRepository.findByName((String) any())).thenReturn(Optional.of(allergen));
        when(allergyRepository.save((Allergy) any())).thenReturn(allergy);
        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        // create expected response
        ExtendedAllergyResponse extendedAllergyResponse = new ExtendedAllergyResponse();
        AllergyResponse allergyResponse = new AllergyResponse(allergen.getId(),allergen, healthRecord.getId());
        extendedAllergyResponse.setAllergyResponse(allergyResponse);
        extendedAllergyResponse.setAllergyCount((long) (healthRecord.getAllergies().size() + 1));

        assertEquals(healthRecordService.addAllergy(addAllergyRequest, patient.getLbp()), extendedAllergyResponse);
    }

    @Test
    void addAllergy_AllergenDoesntExist_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddAllergyRequest addAllergyRequest = new AddAllergyRequest();
        addAllergyRequest.setAllergen("kiwi");
        Allergen allergen = new Allergen();
        allergen.setName("kiwi");
        allergen.setId(1L);

        Allergy allergy = new Allergy();
        allergy.setId(1L);
        allergy.setHealthRecord(healthRecord);
        allergy.setAllergen(allergen);

        // kreiraj add alergy request
        //samo ime alergen-a potrebno
        // lbp

        // mock get allergen from database
        when(allergenRepository.findByName((String) any())).thenReturn(Optional.empty());
        when(allergyRepository.save((Allergy) any())).thenReturn(allergy);
        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        // create expected response
        ExtendedAllergyResponse extendedAllergyResponse = new ExtendedAllergyResponse();
        AllergyResponse allergyResponse = new AllergyResponse(allergen.getId(),allergen, healthRecord.getId());
        extendedAllergyResponse.setAllergyResponse(allergyResponse);
        extendedAllergyResponse.setAllergyCount((long) (healthRecord.getAllergies().size() + 1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addAllergy(addAllergyRequest, patient.getLbp()));
    }

    @Test
    void addAllergy_AllergenAllreadyInUserList_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddAllergyRequest addAllergyRequest = new AddAllergyRequest();
        addAllergyRequest.setAllergen("jaja");
        Allergen allergen = new Allergen();
        allergen.setName("jaja");
        allergen.setId(1L);

        Allergy allergy = new Allergy();
        allergy.setId(1L);
        allergy.setHealthRecord(healthRecord);
        allergy.setAllergen(allergen);


        // mock get allergen from database
        when(allergenRepository.findByName((String) any())).thenReturn(Optional.of(allergen));
        when(allergyRepository.save((Allergy) any())).thenReturn(allergy);
        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        // create expected response
        ExtendedAllergyResponse extendedAllergyResponse = new ExtendedAllergyResponse();
        AllergyResponse allergyResponse = new AllergyResponse(allergen.getId(),allergen, healthRecord.getId());
        extendedAllergyResponse.setAllergyResponse(allergyResponse);
        extendedAllergyResponse.setAllergyCount((long) (healthRecord.getAllergies().size() + 1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addAllergy(addAllergyRequest, patient.getLbp()));
    }


    @Test
    void addVaccination_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine("PRIORIX");
        addVaccinationRequest.setDate(new Date());

        Vaccine vaccine = new Vaccine();
        vaccine.setName("PRIORIX");
        vaccine.setId(1L);
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(1L);
        vaccination.setHealthRecord(healthRecord);
        vaccination.setVaccine(vaccine);
        vaccination.setVaccinationDate(addVaccinationRequest.getDate());

        // kreiraj add alergy request
        //samo ime alergen-a potrebno
        // lbp

        // mock get allergen from database
        when(vaccineRepository.findByName((String) any())).thenReturn(Optional.of(vaccine));
        when(vaccinationRepository.findByHealthRecord((HealthRecord) any())).thenReturn(patient.getHealthRecord().getVaccinations());
        when(vaccinationRepository.save((Vaccination) any())).thenReturn(vaccination);
        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);
        when(patient1.getBirthDate()).thenReturn(patient.getBirthDate());

        // create expected response
        ExtendedVaccinationResponse extendedVaccinationResponse = new ExtendedVaccinationResponse();
        VaccinationResponse vaccinationResponse = new VaccinationResponse(vaccination.getId(), vaccine, healthRecord.getId(), vaccination.getVaccinationDate());
        extendedVaccinationResponse.setVaccinationResponse(vaccinationResponse);
        extendedVaccinationResponse.setVaccinationCount((long) (healthRecord.getVaccinations().size() + 1));

        assertEquals(healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()), extendedVaccinationResponse);
    }

    @Test
    void addVaccination_noDateInRequest_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        healthRecord.setId(123L);
        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine("PRIORIX");
        addVaccinationRequest.setDate(null);

        Vaccine vaccine = new Vaccine();
        vaccine.setName("PRIORIX");
        vaccine.setId(1L);
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(1L);
        vaccination.setHealthRecord(healthRecord);
        vaccination.setVaccine(vaccine);
        vaccination.setVaccinationDate(addVaccinationRequest.getDate());

        // kreiraj add alergy request
        //samo ime alergen-a potrebno
        // lbp

        // mock get allergen from database
        when(vaccineRepository.findByName((String) any())).thenReturn(Optional.of(vaccine));
        when(vaccinationRepository.findByHealthRecord((HealthRecord) any())).thenReturn(patient.getHealthRecord().getVaccinations());
        when(vaccinationRepository.save((Vaccination) any())).thenReturn(vaccination);
        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);
        when(patient1.getBirthDate()).thenReturn(patient.getBirthDate());

        when(healthRecordRepository.save((HealthRecord) any())).thenReturn(healthRecord);

        // create expected response
        ExtendedVaccinationResponse extendedVaccinationResponse = new ExtendedVaccinationResponse();
        VaccinationResponse vaccinationResponse = new VaccinationResponse(vaccination.getId(), vaccine, healthRecord.getId(), vaccination.getVaccinationDate());
        extendedVaccinationResponse.setVaccinationResponse(vaccinationResponse);
        extendedVaccinationResponse.setVaccinationCount((long) (healthRecord.getVaccinations().size() + 1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()));
    }

    @Test
    void addVaccination_VaccineDoesntExist_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine("NONAME");
        addVaccinationRequest.setDate(new Date());

        Vaccine vaccine = new Vaccine();
        vaccine.setName("NONAME");
        vaccine.setId(1L);
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(1L);
        vaccination.setHealthRecord(healthRecord);
        vaccination.setVaccine(vaccine);
        vaccination.setVaccinationDate(addVaccinationRequest.getDate());

        // kreiraj add alergy request
        //samo ime alergen-a potrebno
        // lbp

        // mock get allergen from database
        when(vaccineRepository.findByName((String) any())).thenReturn(Optional.empty());
        when(vaccinationRepository.findByHealthRecord((HealthRecord) any())).thenReturn(patient.getHealthRecord().getVaccinations());
        when(vaccinationRepository.save((Vaccination) any())).thenReturn(vaccination);
        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);
        when(patient1.getBirthDate()).thenReturn(patient.getBirthDate());

        // create expected response
        ExtendedVaccinationResponse extendedVaccinationResponse = new ExtendedVaccinationResponse();
        VaccinationResponse vaccinationResponse = new VaccinationResponse(vaccination.getId(), vaccine, healthRecord.getId(), vaccination.getVaccinationDate());
        extendedVaccinationResponse.setVaccinationResponse(vaccinationResponse);
        extendedVaccinationResponse.setVaccinationCount((long) (healthRecord.getVaccinations().size() + 1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()));
    }

    @Test
    void addVaccination_WritingFutureVaccination_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine("PRIORIX");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date future_date = null;
        try {
            future_date = formatter.parse("31-Dec-9999");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        addVaccinationRequest.setDate(future_date);

        Vaccine vaccine = new Vaccine();
        vaccine.setName("PRIORIX");
        vaccine.setId(1L);
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(1L);
        vaccination.setHealthRecord(healthRecord);
        vaccination.setVaccine(vaccine);
        vaccination.setVaccinationDate(addVaccinationRequest.getDate());

        // kreiraj add alergy request
        //samo ime alergen-a potrebno
        // lbp

        // mock get allergen from database
        when(vaccineRepository.findByName((String) any())).thenReturn(Optional.of(vaccine));
        when(vaccinationRepository.findByHealthRecord((HealthRecord) any())).thenReturn(patient.getHealthRecord().getVaccinations());
        when(vaccinationRepository.save((Vaccination) any())).thenReturn(vaccination);
        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);
        when(patient1.getBirthDate()).thenReturn(patient.getBirthDate());

        // create expected response
        ExtendedVaccinationResponse extendedVaccinationResponse = new ExtendedVaccinationResponse();
        VaccinationResponse vaccinationResponse = new VaccinationResponse(vaccination.getId(), vaccine, healthRecord.getId(), vaccination.getVaccinationDate());
        extendedVaccinationResponse.setVaccinationResponse(vaccinationResponse);
        extendedVaccinationResponse.setVaccinationCount((long) (healthRecord.getVaccinations().size() + 1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()));
    }

    @Test
    void addVaccination_WritingAfterDeathVaccination_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine("PRIORIX");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date future_date = null;
        Date birth_date = null;
        try {
            future_date = formatter.parse("30-Dec-2020");
            birth_date = formatter.parse("30-Dec-2000");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        patient.setBirthDate(birth_date);
        patient.setDeathDate(future_date);

        addVaccinationRequest.setDate(new Date());
        Vaccine vaccine = new Vaccine();
        vaccine.setName("PRIORIX");
        vaccine.setId(1L);
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(1L);
        vaccination.setHealthRecord(healthRecord);
        vaccination.setVaccine(vaccine);
        vaccination.setVaccinationDate(addVaccinationRequest.getDate());

        // kreiraj add alergy request
        //samo ime alergen-a potrebno
        // lbp

        // mock get allergen from database
        when(vaccineRepository.findByName((String) any())).thenReturn(Optional.of(vaccine));
        when(vaccinationRepository.findByHealthRecord((HealthRecord) any())).thenReturn(patient.getHealthRecord().getVaccinations());
        when(vaccinationRepository.save((Vaccination) any())).thenReturn(vaccination);
        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);
        when(patient1.getBirthDate()).thenReturn(patient.getBirthDate());
        when(patient1.getDeathDate()).thenReturn(patient.getDeathDate());

        // create expected response
        ExtendedVaccinationResponse extendedVaccinationResponse = new ExtendedVaccinationResponse();
        VaccinationResponse vaccinationResponse = new VaccinationResponse(vaccination.getId(), vaccine, healthRecord.getId(), vaccination.getVaccinationDate());
        extendedVaccinationResponse.setVaccinationResponse(vaccinationResponse);
        extendedVaccinationResponse.setVaccinationCount((long) (healthRecord.getVaccinations().size() + 1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()));
    }

    @Test
    void addVaccination_addVaccinationBeforePatientBirth_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine("PRIORIX");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date future_date;
        try {
            future_date = formatter.parse("31-Dec-1000");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        addVaccinationRequest.setDate(future_date);

        Vaccine vaccine = new Vaccine();
        vaccine.setName("PRIORIX");
        vaccine.setId(1L);
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(1L);
        vaccination.setHealthRecord(healthRecord);
        vaccination.setVaccine(vaccine);
        vaccination.setVaccinationDate(addVaccinationRequest.getDate());

        // kreiraj add alergy request
        //samo ime alergen-a potrebno
        // lbp

        // mock get allergen from database
        when(vaccineRepository.findByName((String) any())).thenReturn(Optional.of(vaccine));
        when(vaccinationRepository.findByHealthRecord((HealthRecord) any())).thenReturn(patient.getHealthRecord().getVaccinations());
        when(vaccinationRepository.save((Vaccination) any())).thenReturn(vaccination);
        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);
        when(patient1.getBirthDate()).thenReturn(patient.getBirthDate());

        // create expected response
        ExtendedVaccinationResponse extendedVaccinationResponse = new ExtendedVaccinationResponse();
        VaccinationResponse vaccinationResponse = new VaccinationResponse(vaccination.getId(), vaccine, healthRecord.getId(), vaccination.getVaccinationDate());
        extendedVaccinationResponse.setVaccinationResponse(vaccinationResponse);
        extendedVaccinationResponse.setVaccinationCount((long) (healthRecord.getVaccinations().size() + 1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()));
    }

    @Test
    void createExaminationReportRequest_Success() {
        // kreiraj request
        CreateExaminationReportRequest createExaminationReportRequest = new CreateExaminationReportRequest();
        createExaminationReportRequest.setObjectiveFinding("novak je objektivan");
        createExaminationReportRequest.setMainSymptoms("nema simptoma");
        createExaminationReportRequest.setCurrentIllness("nema boljku");
        createExaminationReportRequest.setDiagnosis("I35.0");
        createExaminationReportRequest.setExistingDiagnosis(Boolean.FALSE);
        createExaminationReportRequest.setTreatmentResult("U toku");
        createExaminationReportRequest.setCurrentStateDescription("mora da ima ovo polje");

        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Patient patient1 = mock(Patient.class);

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("I35.0");
        diagnosis.setDescription("ujed latino komarca");
        diagnosis.setLatinDescription("latino dasa");

        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        when(medicalHistoryRepository.findByHealthRecord((HealthRecord) any() )).thenReturn(patient.getHealthRecord().getMedicalHistory());

        when(diagnosisRepository.findByCode((String) any())).thenReturn(Optional.of(diagnosis));

        // kreiraj ocekivani response
        MessageResponse messageResponse = new MessageResponse("uspesno upisan pregled");

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayload());

            assertEquals(messageResponse, healthRecordService.createExaminationReportRequest(patient.getLbp(),
                                                                        patient.getLbp(),
                                                                        createExaminationReportRequest));
        }
    }

    @Test
    void createExaminationReportRequest_mkb10CodeNotFount_ThrowException() {
        // kreiraj request
        CreateExaminationReportRequest createExaminationReportRequest = new CreateExaminationReportRequest();
        createExaminationReportRequest.setObjectiveFinding("novak je objektivan");
        createExaminationReportRequest.setMainSymptoms("nema simptoma");
        createExaminationReportRequest.setCurrentIllness("nema boljku");
        createExaminationReportRequest.setDiagnosis("I35.0");
        createExaminationReportRequest.setExistingDiagnosis(Boolean.FALSE);
        createExaminationReportRequest.setTreatmentResult("U toku");
        createExaminationReportRequest.setCurrentStateDescription("mora da ima ovo polje");

        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Patient patient1 = mock(Patient.class);

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("I35.0");
        diagnosis.setDescription("ujed latino komarca");
        diagnosis.setLatinDescription("latino dasa");

        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        when(medicalHistoryRepository.findByHealthRecord((HealthRecord) any() )).thenReturn(patient.getHealthRecord().getMedicalHistory());

        when(diagnosisRepository.findByCode((String) any())).thenReturn(Optional.empty());

        // kreiraj ocekivani response
        MessageResponse messageResponse = new MessageResponse("uspesno upisan pregled");

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayload());

            assertThrows(BadRequestException.class, () -> healthRecordService.createExaminationReportRequest(patient.getLbp(),
                    patient.getLbp(),
                    createExaminationReportRequest));
        }
    }


    @Test
    void createExaminationReportRequest_NoPermissionConfidential_ThrowException() {
        // kreiraj request
        CreateExaminationReportRequest createExaminationReportRequest = new CreateExaminationReportRequest();
        createExaminationReportRequest.setObjectiveFinding("novak je objektivan");
        createExaminationReportRequest.setMainSymptoms("nema simptoma");
        createExaminationReportRequest.setCurrentIllness("nema boljku");
        createExaminationReportRequest.setDiagnosis("nepoznati_kod");
        createExaminationReportRequest.setExistingDiagnosis(Boolean.FALSE);
        createExaminationReportRequest.setTreatmentResult("U toku");
        createExaminationReportRequest.setConfidential(Boolean.TRUE);
        createExaminationReportRequest.setCurrentStateDescription("mora da ima ovo polje");

        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Patient patient1 = mock(Patient.class);

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("nepoznati_kod");
        diagnosis.setDescription("ujed latino komarca");
        diagnosis.setLatinDescription("latino dasa");

        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        when(medicalHistoryRepository.findByHealthRecord((HealthRecord) any() )).thenReturn(patient.getHealthRecord().getMedicalHistory());

        when(diagnosisRepository.findByCode((String) any())).thenReturn(Optional.of(diagnosis));

        // kreiraj ocekivani response
        MessageResponse messageResponse = new MessageResponse("uspesno upisan pregled");

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayload());

            assertThrows(BadRequestException.class, () -> healthRecordService.createExaminationReportRequest(patient.getLbp(),
                    patient.getLbp(),
                    createExaminationReportRequest));
        }
    }

    @Test
    void createExaminationReportRequest_ExistingDiagnossis_Success() {
        // kreiraj request
        CreateExaminationReportRequest createExaminationReportRequest = new CreateExaminationReportRequest();
        createExaminationReportRequest.setObjectiveFinding("novak je objektivan");
        createExaminationReportRequest.setMainSymptoms("nema simptoma");
        createExaminationReportRequest.setCurrentIllness("nema boljku");
        createExaminationReportRequest.setDiagnosis("I35.0");
        createExaminationReportRequest.setExistingDiagnosis(Boolean.TRUE);
        createExaminationReportRequest.setTreatmentResult("U toku");
        createExaminationReportRequest.setAdvice("bez konzumacije alkohola");
        createExaminationReportRequest.setAnamnesis("nema anamnezija");
        createExaminationReportRequest.setFamilyAnamnesis("nema porodicnih anamnezija");
        createExaminationReportRequest.setPatientOpinion("pacijent se dobro oseca");
        createExaminationReportRequest.setSuggestedTherapy("plivanje jednom nedeljno");
        createExaminationReportRequest.setCurrentStateDescription("mora da ima ovo polje");

        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Patient patient1 = mock(Patient.class);

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("I35.0");
        diagnosis.setDescription("ujed latino komarca");
        diagnosis.setLatinDescription("latino dasa");

        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        when(medicalHistoryRepository.findByHealthRecord((HealthRecord) any() )).thenReturn(patient.getHealthRecord().getMedicalHistory());

        when(diagnosisRepository.findByCode((String) any())).thenReturn(Optional.of(diagnosis));

        // kreiraj ocekivani response
        MessageResponse messageResponse = new MessageResponse("uspesno upisan pregled");

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayload());

            assertEquals(messageResponse, healthRecordService.createExaminationReportRequest(patient.getLbp(),
                    patient.getLbp(),
                    createExaminationReportRequest));
        }
    }

    @Test
    void createExaminationReportRequest_ExistingDiagnosisEmpty_ThrowException() {
        // kreiraj request
        CreateExaminationReportRequest createExaminationReportRequest = new CreateExaminationReportRequest();
        createExaminationReportRequest.setObjectiveFinding("novak je objektivan");
        createExaminationReportRequest.setMainSymptoms("nema simptoma");
        createExaminationReportRequest.setCurrentIllness("nema boljku");
        createExaminationReportRequest.setDiagnosis("nepoznati_kod");
        createExaminationReportRequest.setTreatmentResult("U toku");
        createExaminationReportRequest.setCurrentStateDescription("mora da ima ovo polje");

        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Patient patient1 = mock(Patient.class);

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("nepoznati_kod");
        diagnosis.setDescription("ujed latino komarca");
        diagnosis.setLatinDescription("latino dasa");

        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        when(medicalHistoryRepository.findByHealthRecord((HealthRecord) any() )).thenReturn(patient.getHealthRecord().getMedicalHistory());

        when(diagnosisRepository.findByCode((String) any())).thenReturn(Optional.of(diagnosis));


        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayload());

            assertThrows(BadRequestException.class, () -> healthRecordService.createExaminationReportRequest(patient.getLbp(),
                    patient.getLbp(),
                    createExaminationReportRequest));
        }
    }

    @Test
    void createExaminationReportRequest_NotExistingDiagnosis_ThrowException() {
        // kreiraj request
        CreateExaminationReportRequest createExaminationReportRequest = new CreateExaminationReportRequest();
        createExaminationReportRequest.setObjectiveFinding("novak je objektivan");
        createExaminationReportRequest.setMainSymptoms("nema simptoma");
        createExaminationReportRequest.setCurrentIllness("nema boljku");
        createExaminationReportRequest.setDiagnosis("nepoznati_kod");
        createExaminationReportRequest.setExistingDiagnosis(Boolean.TRUE);
        createExaminationReportRequest.setTreatmentResult("U toku");
        createExaminationReportRequest.setCurrentStateDescription("mora da ima ovo polje");

        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        Patient patient1 = mock(Patient.class);

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("nepoznati_kod");
        diagnosis.setDescription("ujed latino komarca");
        diagnosis.setLatinDescription("latino dasa");

        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        when(medicalHistoryRepository.findByHealthRecord((HealthRecord) any() )).thenReturn(patient.getHealthRecord().getMedicalHistory());

        when(diagnosisRepository.findByCode((String) any())).thenReturn(Optional.of(diagnosis));

        try(MockedStatic<TokenPayloadUtil> tokenUtils = Mockito.mockStatic(TokenPayloadUtil.class)){
            tokenUtils.when(TokenPayloadUtil::getTokenPayload).thenReturn(makeTokenPayload());

            assertThrows(BadRequestException.class, () -> healthRecordService.createExaminationReportRequest(patient.getLbp(),
                    patient.getLbp(),
                    createExaminationReportRequest));
        }
    }

    @Test
    void updateHealthRecord_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        healthRecord.setId(123L);
        UpdateHealthRecordRequest updateHealthRecordRequest = new UpdateHealthRecordRequest();
        updateHealthRecordRequest.setBlodtype("A");
        updateHealthRecordRequest.setRhfactor("-");


        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);
        when(healthRecordRepository.save(any())).thenReturn(healthRecord);

        // create expected response
        BasicHealthRecordResponse basicHealthRecordResponse = new BasicHealthRecordResponse();
        basicHealthRecordResponse.setBloodType(BloodType.A);
        basicHealthRecordResponse.setRhFactor(RHFactor.MINUS);
        basicHealthRecordResponse.setPatientLbp(patient.getLbp());
        basicHealthRecordResponse.setId(healthRecord.getId());


        assertEquals(healthRecordService.updateHealthRecord(updateHealthRecordRequest, patient.getLbp()), basicHealthRecordResponse);
    }

    @Test
    void updateHealthRecord_UserWithoutHealthrecord_ThrowsException(){
        Patient patient = makePatient();
        patient.setHealthRecord(null);
        HealthRecord healthRecord = patient.getHealthRecord();
        UpdateHealthRecordRequest updateHealthRecordRequest = new UpdateHealthRecordRequest();
        updateHealthRecordRequest.setBlodtype("A");
        updateHealthRecordRequest.setRhfactor("-");


        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);
        when(healthRecordRepository.save(any())).thenReturn(healthRecord);

        assertThrows(InternalServerErrorException.class, () -> healthRecordService.updateHealthRecord(updateHealthRecordRequest, patient.getLbp()));
    }

    @Test
    void updateHealthRecord_WrongBloodType_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        healthRecord.setId(1L);
        UpdateHealthRecordRequest updateHealthRecordRequest = new UpdateHealthRecordRequest();
        updateHealthRecordRequest.setBlodtype("C");
        updateHealthRecordRequest.setRhfactor("-");


        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        // create expected response
        BasicHealthRecordResponse basicHealthRecordResponse = new BasicHealthRecordResponse();
        basicHealthRecordResponse.setBloodType(BloodType.A);
        basicHealthRecordResponse.setRhFactor(RHFactor.MINUS);
        basicHealthRecordResponse.setPatientLbp(patient.getLbp());
        basicHealthRecordResponse.setId(healthRecord.getId());


        assertThrows(BadRequestException.class, () -> healthRecordService.updateHealthRecord(updateHealthRecordRequest, patient.getLbp()));
    }

    @Test
    void updateHealthRecord_WrongRhfactor_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        healthRecord.setId(1L);
        UpdateHealthRecordRequest updateHealthRecordRequest = new UpdateHealthRecordRequest();
        updateHealthRecordRequest.setBlodtype("AB");
        updateHealthRecordRequest.setRhfactor("+-");


        // mock get healthrecord
        Patient patient1 = mock(Patient.class);
        when(patient1.getHealthRecord()).thenReturn(healthRecord);
        when(patientService.findPatient((UUID) any())).thenReturn(patient1);

        // create expected response
        BasicHealthRecordResponse basicHealthRecordResponse = new BasicHealthRecordResponse();
        basicHealthRecordResponse.setBloodType(BloodType.A);
        basicHealthRecordResponse.setRhFactor(RHFactor.MINUS);
        basicHealthRecordResponse.setPatientLbp(patient.getLbp());
        basicHealthRecordResponse.setId(healthRecord.getId());


        assertThrows(BadRequestException.class, () -> healthRecordService.updateHealthRecord(updateHealthRecordRequest, patient.getLbp()));
    }

    private Patient makePatient(){
        HealthRecord healthRecord = makeHealthRecord();
        Patient patient = new Patient();
        patient.setBirthDate(new Date());
        patient.setHealthRecord(healthRecord);
        return patient;
    }

    private Allergen makeAllergen() {
        Allergen allergen = new Allergen();
        allergen.setName("jaja");
        allergen.setId(0L);
        return allergen;
    }

    private Allergy makeAllergy() {
        Allergy allergy = new Allergy();
        allergy.setAllergen(makeAllergen());
        allergy.setId(0L);
        return allergy;
    }

    private HealthRecord makeHealthRecord(){
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setId(0L);

        healthRecord.setBloodType(BloodType.A);
        healthRecord.setRhFactor(RHFactor.PLUS);
        healthRecord.setRegistrationDate(new Date());

        Allergy allergy = makeAllergy();
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
