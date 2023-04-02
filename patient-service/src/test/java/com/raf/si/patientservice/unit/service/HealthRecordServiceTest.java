package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.AddAllergyRequest;
import com.raf.si.patientservice.dto.request.AddVaccinationRequest;
import com.raf.si.patientservice.dto.request.CreateExaminationReportRequest;
import com.raf.si.patientservice.dto.request.MedicalExaminationFilterRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.mapper.HealthRecordMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.model.enums.medicalhistory.TreatmentResult;
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

    private DiagnosisRepository diagnosisRepository;

    private VaccineRepository vaccineRepository;

    private AllergenRepository allergenRepository;
    private HealthRecordRepository healthRecordRepository;

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

    @Test
    public void getAvailableAllergens_Success(){
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
    public void getAvailableVaccines_Success(){
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
    public void addAllergy_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddAllergyRequest addAllergyRequest = new AddAllergyRequest();
        addAllergyRequest.setAllergen("mleko");
        Allergen allergen = new Allergen();
        allergen.setName("mleko");
        allergen.setId(new Long(1));

        Allergy allergy = new Allergy();
        allergy.setId(new Long(1));
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
        extendedAllergyResponse.setAllergyCount(new Long(healthRecord.getAllergies().size()+1));

        assertEquals(healthRecordService.addAllergy(addAllergyRequest, patient.getLbp()), extendedAllergyResponse);
    }

    @Test
    public void addAllergy_AllergenDoesntExist_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddAllergyRequest addAllergyRequest = new AddAllergyRequest();
        addAllergyRequest.setAllergen("kiwi");
        Allergen allergen = new Allergen();
        allergen.setName("kiwi");
        allergen.setId(new Long(1));

        Allergy allergy = new Allergy();
        allergy.setId(new Long(1));
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
        extendedAllergyResponse.setAllergyCount(new Long(healthRecord.getAllergies().size()+1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addAllergy(addAllergyRequest, patient.getLbp()));
    }

    @Test
    public void addAllergy_AllergenAllreadyInUserList_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddAllergyRequest addAllergyRequest = new AddAllergyRequest();
        addAllergyRequest.setAllergen("jaja");
        Allergen allergen = new Allergen();
        allergen.setName("jaja");
        allergen.setId(new Long(1));

        Allergy allergy = new Allergy();
        allergy.setId(new Long(1));
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
        extendedAllergyResponse.setAllergyCount(new Long(healthRecord.getAllergies().size()+1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addAllergy(addAllergyRequest, patient.getLbp()));
    }


    @Test
    public void addVaccination_Success(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine("PRIORIX");
        addVaccinationRequest.setDate(new Date());

        Vaccine vaccine = new Vaccine();
        vaccine.setName("PRIORIX");
        vaccine.setId(new Long(1));
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(new Long(1));
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
        extendedVaccinationResponse.setVaccinationCount(new Long(healthRecord.getVaccinations().size()+1));

        assertEquals(healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()), extendedVaccinationResponse);
    }

    @Test
    public void addVaccination_noDateInRequest_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine("PRIORIX");
        addVaccinationRequest.setDate(null);

        Vaccine vaccine = new Vaccine();
        vaccine.setName("PRIORIX");
        vaccine.setId(new Long(1));
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(new Long(1));
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
        extendedVaccinationResponse.setVaccinationCount(new Long(healthRecord.getVaccinations().size()+1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()));
    }

    @Test
    public void addVaccination_VaccineDoesntExist_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine("NONAME");
        addVaccinationRequest.setDate(new Date());

        Vaccine vaccine = new Vaccine();
        vaccine.setName("NONAME");
        vaccine.setId(new Long(1));
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(new Long(1));
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
        extendedVaccinationResponse.setVaccinationCount(new Long(healthRecord.getVaccinations().size()+1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()));
    }

    @Test
    public void addVaccination_WritingFutureVaccination_ThrowsException(){
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
        vaccine.setId(new Long(1));
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(new Long(1));
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
        extendedVaccinationResponse.setVaccinationCount(new Long(healthRecord.getVaccinations().size()+1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()));
    }

    @Test
    public void addVaccination_addVaccinationBeforePatientBirth_ThrowsException(){
        Patient patient = makePatient();
        HealthRecord healthRecord = patient.getHealthRecord();
        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine("PRIORIX");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date future_date = null;
        try {
            future_date = formatter.parse("31-Dec-1000");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        addVaccinationRequest.setDate(future_date);

        Vaccine vaccine = new Vaccine();
        vaccine.setName("PRIORIX");
        vaccine.setId(new Long(1));
        vaccine.setProducer("GlaxoSmithKline Biologicals S.A.");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv morbila");

        Vaccination vaccination = new Vaccination();
        vaccination.setId(new Long(1));
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
        extendedVaccinationResponse.setVaccinationCount(new Long(healthRecord.getVaccinations().size()+1));

        assertThrows(BadRequestException.class, () -> healthRecordService.addVaccination(addVaccinationRequest, patient.getLbp()));
    }

    // @Test
    // public void createExaminationReportRequest_Success() {
    //     // kreiraj request
    //     CreateExaminationReportRequest createExaminationReportRequest = new CreateExaminationReportRequest();
    //     createExaminationReportRequest.setObjectiveFinding("novak je objektivan");
    //     createExaminationReportRequest.setMainSymptoms("nema simptoma");
    //     createExaminationReportRequest.setCurrentIllness("nema boljku");
    //     createExaminationReportRequest.setDiagnosis("I35.0");
    //     createExaminationReportRequest.setExistingDiagnosis(Boolean.FALSE);
    //     createExaminationReportRequest.setTreatmentResult("U toku");
    //     createExaminationReportRequest.setCurrentStateDescription("mora da ima ovo polje");

    //     Patient patient = makePatient();



    //     // kreiraj ocekivani response
    //     MessageResponse messageResponse = new MessageResponse("uspesno upisan pregled");

    //     // assertEquals(messageResponse, healthRecordService.createExaminationReportRequest(patient.getLbp(), patientcreateExaminationReportRequest));
    // }

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
        allergen.setId(new Long(0));
        return allergen;
    }

    private Allergy makeAllergy() {
        Allergy allergy = new Allergy();
        allergy.setAllergen(makeAllergen());
        allergy.setId(new Long(0));
        return allergy;
    }

    private HealthRecord makeHealthRecord(){
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setId(new Long(0));

        healthRecord.setBloodType(BloodType.A);
        healthRecord.setRhFactor(RHFactor.PLUS);
        healthRecord.setRegistrationDate(new Date());

        Allergy allergy = makeAllergy();
        Operation operation = new Operation();
        MedicalHistory history = new MedicalHistory();
        MedicalExamination examination = new MedicalExamination();
        Vaccination vaccination = new Vaccination();

        List<Allergy> allergies = new ArrayList<>();
        allergies.add(allergy);
        List<Operation> operations = Arrays.asList(new Operation[] {operation});
        List<MedicalHistory> medicalHistoryList = Arrays.asList(new MedicalHistory[] {history});
        List<MedicalExamination> examinations = Arrays.asList(new MedicalExamination[] {examination});
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
