package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.*;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.HealthRecordMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.repository.*;
import com.raf.si.patientservice.repository.filtering.filter.MedicalExaminationFilter;
import com.raf.si.patientservice.repository.filtering.filter.MedicalHistoryFilter;
import com.raf.si.patientservice.repository.filtering.specification.MedicalExaminationSpecification;
import com.raf.si.patientservice.repository.filtering.specification.MedicalHistorySpecification;
import com.raf.si.patientservice.service.HealthRecordService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import java.util.*;

@Slf4j
@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    private static final String PERMITTED_DOC = "ROLE_DR_SPEC_POV";

    private final AllergyRepository allergyRepository;

    private final AllergenRepository allergenRepository;

    private final VaccineRepository vaccineRepository;
    private final VaccinationRepository vaccinationRepository;
    private final MedicalExaminationRepository medicalExaminationRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final OperationRepository operationRepository;

    private final HealthRecordRepository healthRecordRepository;

    private final DiagnosisRepository diagnosisRepository;

    private final PatientService patientService;

    private final HealthRecordMapper healthRecordMapper;


    public HealthRecordServiceImpl(AllergyRepository allergyRepository,
                                   DiagnosisRepository diagnosisRepository,
                                   AllergenRepository allergenRepository,
                                   VaccineRepository vaccineRepository,
                                   VaccinationRepository vaccinationRepository,
                                   MedicalExaminationRepository medicalExaminationRepository,
                                   MedicalHistoryRepository medicalHistoryRepository,
                                   OperationRepository operationRepository,
                                   HealthRecordRepository healthRecordRepository,
                                   PatientService patientService,
                                   HealthRecordMapper healthRecordMapper) {
        this.diagnosisRepository = diagnosisRepository;
        this.allergyRepository = allergyRepository;
        this.allergenRepository = allergenRepository;
        this.vaccineRepository = vaccineRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.medicalExaminationRepository = medicalExaminationRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.operationRepository = operationRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.patientService = patientService;
        this.healthRecordMapper = healthRecordMapper;
    }

    @Override
    public HealthRecordResponse getHealthRecordForPatient(UUID lbp, Pageable pageable) {
        Patient patient = patientService.findPatient(lbp);
        HealthRecord healthRecord = patient.getHealthRecord();
        Page<Allergy> allergies = allergyRepository.findByHealthRecord(healthRecord, pageable);
        Page<Vaccination> vaccinations = vaccinationRepository.findByHealthRecord(healthRecord, pageable);
        Page<Operation> operations = operationRepository.findByHealthRecord(healthRecord, pageable);

        Page<MedicalExamination> examinations = getMedicalExaminationPage(healthRecord,
                null,
                null,
                pageable);
        Page<MedicalHistory> medicalHistory = getMedicalHistoryPage(healthRecord,
                null,
                pageable);

        HealthRecordResponse response = healthRecordMapper.healthRecordToHealthRecordResponse(patient,
                healthRecord,
                allergies,
                vaccinations,
                examinations,
                medicalHistory,
                operations);

        return response;
    }

    @Override
    public LightHealthRecordResponse getLightHealthRecordForPatient(UUID lbp, Pageable pageable) {
        Patient patient = patientService.findPatient(lbp);
        HealthRecord healthRecord = patient.getHealthRecord();
        Page<Allergy> allergies = allergyRepository.findByHealthRecord(healthRecord, pageable);
        Page<Vaccination> vaccinations = vaccinationRepository.findByHealthRecord(healthRecord, pageable);

        return healthRecordMapper.healthRecordToLightHealthRecordResponse(patient,
                healthRecord,
                allergies,
                vaccinations);
    }

    @Override
    public MedicalExaminationListResponse findExaminations(UUID lbp, MedicalExaminationFilterRequest request, Pageable pageable) {
        Patient patient = patientService.findPatient(lbp);
        HealthRecord healthRecord = patient.getHealthRecord();

        Page<MedicalExamination> examinations = getMedicalExaminationPage(healthRecord,
                request.getStartDate(),
                request.getEndDate(),
                pageable);

        return healthRecordMapper.getPermittedExaminations(examinations);
    }

    @Override
    public MedicalHistoryListResponse findMedicalHistory(UUID lbp, String diagnosisCode, Pageable pageable) {
        Patient patient = patientService.findPatient(lbp);
        HealthRecord healthRecord = patient.getHealthRecord();

        Page<MedicalHistory> medicalHistory = getMedicalHistoryPage(healthRecord,
                diagnosisCode,
                pageable);

        return healthRecordMapper.getPermittedMedicalHistory(medicalHistory);
    }



    private Page<MedicalExamination> getMedicalExaminationPage(HealthRecord healthRecord,
                                                               Date startDate,
                                                               Date endDate,
                                                               Pageable pageable){

        if(startDate != null){
            startDate = DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
            if(endDate == null)
                DateUtils.addDays(startDate, 1);
            else{
                endDate = DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH);
                endDate = DateUtils.addDays(endDate, 1);
            }
        }

        MedicalExaminationFilter filter = new MedicalExaminationFilter(healthRecord,
                startDate,
                endDate,
                canGetConfidential());
        MedicalExaminationSpecification spec = new MedicalExaminationSpecification(filter);
        return medicalExaminationRepository.findAll(spec, pageable);
    }

    private Page<MedicalHistory> getMedicalHistoryPage(HealthRecord healthRecord,
                                                       String diagnosisCode,
                                                       Pageable pageable){

        MedicalHistoryFilter filter = new MedicalHistoryFilter(healthRecord, diagnosisCode, canGetConfidential());
        MedicalHistorySpecification spec = new MedicalHistorySpecification(filter);
        return medicalHistoryRepository.findAll(spec, pageable);
    }

    private boolean canGetConfidential(){
        return TokenPayloadUtil.getTokenPayload()
                .getPermissions()
                .stream()
                .anyMatch(permission -> permission.equals(PERMITTED_DOC));
    }

    private HealthRecord getRecordByLbp(UUID lbp) {
        // dohvati iz baze korisnika
        Patient patient = patientService.findPatient(lbp);
        HealthRecord healthRecord = patient.getHealthRecord();
        if(healthRecord == null) {
            String errMessage = String.format("pacijent sa lbp '%s' nema zdravstveni karton", lbp);
            log.info(errMessage);
            throw new InternalServerErrorException(errMessage);
        }
        return healthRecord;
    }

    @Override
    public BasicHealthRecordResponse updateHealthRecord(UpdateHealthRecordRequest updateHealthRecordRequest, UUID lbp) {

        // getrecordbylbp
        HealthRecord healthRecord = getRecordByLbp(lbp);

        //update podatke
        healthRecord = healthRecordMapper.updateHealthRecordRequestToHealthRecord( updateHealthRecordRequest, healthRecord);

        // update podatke u bazi
        healthRecord = healthRecordRepository.save(healthRecord);

        BasicHealthRecordResponse basicHealthRecordResponse = healthRecordMapper.healthRecordToBasicHealthRecordResponse(lbp, healthRecord);

        return basicHealthRecordResponse;
    }

    @Override
    public ExtendedAllergyResponse addAllergy(AddAllergyRequest addAllergyRequest, UUID lbp) {

        // proveri da li su vrednosti koje je korisnik poslao dobre
        Allergen allergen = allergenRepository.findByName(addAllergyRequest.getAllergen())
                .orElseThrow(() -> {
                    String errMessage = String.format("alergent '%s' ne postoji", addAllergyRequest.getAllergen());
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        // getrecordbylbp
        HealthRecord healthRecord = getRecordByLbp(lbp);

        // proveri da li je vec uneta alergija
        for(Allergy allergy : healthRecord.getAllergies()) {
            if(allergy.getAllergen().getName().equals(addAllergyRequest.getAllergen())){
                String errMessage = String.format("alergent '%s' je vec upisan za korisnika '%s'", addAllergyRequest.getAllergen(), lbp);
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
        }

        //update podatke
        Allergy allergy = healthRecordMapper.addAllergyRequestToAllergy(addAllergyRequest, healthRecord, allergen);

        // update podatke u bazi
        allergy = allergyRepository.save(allergy);

        healthRecord.getAllergies().add(allergy);

        ExtendedAllergyResponse extendedAllergyResponse = healthRecordMapper.allergyToExtendedAllergyResponse(healthRecord, allergy);
        return extendedAllergyResponse;
    }

    @Override
    public ExtendedVaccinationResponse addVaccination(AddVaccinationRequest addVaccinationRequest, UUID lbp) {


        // proveri da li su vrednosti koje je korisnik poslao dobre
        Vaccine vaccine = vaccineRepository.findByName(addVaccinationRequest.getVaccine())
                .orElseThrow(() -> {
                    String errMessage = String.format("vakcina sa nazivom '%s' ne postoji", addVaccinationRequest.getVaccine());
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        // proveri da li je datum manji od trenutnog vremena
        Date vaccinationDate = addVaccinationRequest.getDate();
        if (vaccinationDate==null) {
            String errMessage = String.format("polje 'date' ne sme da bude prazno", addVaccinationRequest.getVaccine());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        if(vaccinationDate.compareTo(new Date()) > 0){
            String errMessage = String.format("nije moguce upisati buducu vakcinaciju");
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        // getrecordbylbp
        HealthRecord healthRecord = getRecordByLbp(lbp);


        // proveriti da li je vakcinacija tokom lifetime-a pacijenta
        Patient patient = patientService.findPatient(lbp);
        if(vaccinationDate.compareTo(patient.getBirthDate()) < 0
                ||
                (
                        patient.getDeathDate() != null
                                &&
                                vaccinationDate.compareTo(patient.getDeathDate())>0
                )) {
            String errMessage = String.format("datum vakcinacije mora biti izmedju rodjenja i smrti pacijenta");
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }



        //update podatke
        Vaccination vaccination = healthRecordMapper.addVaccinationRequestToVaccinatin(addVaccinationRequest, healthRecord, vaccine);

        healthRecord.getVaccinations().add(vaccination);

        // update podatke u bazi
        vaccination = vaccinationRepository.save(vaccination);

        ExtendedVaccinationResponse extendedVaccinationResponse = healthRecordMapper.vaccinationToExtendedVaccinationResponse(healthRecord, vaccination);

        return extendedVaccinationResponse;
    }

    @Override
    public VaccineListResponse getAvailableVaccines() {
        List<Vaccine> vaccines = vaccineRepository.findAll();
        return healthRecordMapper.vaccineListToVaccineListResponse(vaccines);
    }

    @Override
    public AllergenListResponse getAvailableAllergens() {
        List<Allergen> vaccines = allergenRepository.findAll();
        return healthRecordMapper.allergenListToAllergenListResponse(vaccines);
    }

    @Override
    public MessageResponse createExaminationReportRequest(UUID lbp, UUID lbz, CreateExaminationReportRequest createExaminationReportRequest) {

        // ako ima confidential, onda proveriti da je auth doktor specijalista
        if(createExaminationReportRequest.getConfidential()!=null && createExaminationReportRequest.getConfidential().equals(Boolean.TRUE) && !canGetConfidential()){
            String errMessage = String.format("ovaj korisnik ne moze kreirati confidential examination");
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        HealthRecord healthRecord = getRecordByLbp(lbp);
        Diagnosis diagnosis = null;
        if(createExaminationReportRequest.getDiagnosis() != null) {
            diagnosis = diagnosisRepository.findByCode(createExaminationReportRequest.getDiagnosis()).orElseThrow(() -> {
                String errMessage = String.format("diagnosis mkb10 code '%s' not found in database", createExaminationReportRequest.getDiagnosis());
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            });
        }

        MedicalExamination medicalExamination = healthRecordMapper.createExaminationReportRequestToExamination(lbp,
                lbz,healthRecord,
                createExaminationReportRequest,
                diagnosis);

        medicalExamination = medicalExaminationRepository.save(medicalExamination);

        // ako ima dijagnozu
        if(medicalExamination.getDiagnosis() != null) {

            if(createExaminationReportRequest.getExistingDiagnosis() == null) {
                String errMessage = String.format("polje 'existingDiagnosis' ne sme biti prazno");
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }

            if(createExaminationReportRequest.getExistingDiagnosis()) {

                // nadji stari
                List<MedicalHistory> oldMedicalHistoryList = medicalHistoryRepository.findByHealthRecord(healthRecord);
                if(oldMedicalHistoryList == null ) {
                    String errMessage = String.format("nije uspeo da dohvati istoriju bolesti za korisnika '%s'", lbp);
                    log.info(errMessage);
                    throw new InternalServerErrorException(errMessage);
                }
                MedicalHistory oldMedicalHistory = null;
                for(MedicalHistory medicalHistory : oldMedicalHistoryList) {
                    if( medicalHistory.getDiagnosis().getCode().equals(medicalExamination.getDiagnosis().getCode())){
                        oldMedicalHistory = medicalHistory;
                    }
                }
                if(oldMedicalHistory==null) {
                    String errMessage = String.format("korisnik '%s' nema postojecu dijagnozu '%s'", lbp, medicalExamination.getDiagnosis().getCode());
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                }

                // kreiraj novi
                MedicalHistory medicalHistory = healthRecordMapper.medicalExaminationToMedicalHistory(medicalExamination, healthRecord, oldMedicalHistory, createExaminationReportRequest);

                // izmeni validnost starog record-a
                oldMedicalHistory.setValid(false);
                oldMedicalHistory.setValidUntil(new Date());

                // zapamti novi i stari
                medicalHistoryRepository.save(oldMedicalHistory);
                medicalHistoryRepository.save(medicalHistory);
            }
            else {
                // kreiraj novi
                MedicalHistory medicalHistory = healthRecordMapper.medicalExaminationToMedicalHistory(medicalExamination, healthRecord, null, createExaminationReportRequest);

                // zapamti novi
                medicalHistoryRepository.save(medicalHistory);
            }
        }

        return new MessageResponse("uspesno upisan pregled");
    }

}
