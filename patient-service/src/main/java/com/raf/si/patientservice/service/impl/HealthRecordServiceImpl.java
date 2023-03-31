package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.AddAllergyRequest;
import com.raf.si.patientservice.dto.request.AddVaccinationRequest;
import com.raf.si.patientservice.dto.request.MedicalExaminationFilterRequest;
import com.raf.si.patientservice.dto.request.UpdateHealthRecordRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.HealthRecordMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.repository.*;
import com.raf.si.patientservice.repository.filtering.filter.MedicalExaminationFilter;
import com.raf.si.patientservice.repository.filtering.filter.MedicalHistoryFilter;
import com.raf.si.patientservice.repository.filtering.specification.MedicalExaminationSpecification;
import com.raf.si.patientservice.repository.filtering.specification.MedicalHistorySpecification;
import com.raf.si.patientservice.service.HealthRecordService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    private final PatientService patientService;

    private final HealthRecordMapper healthRecordMapper;


    public HealthRecordServiceImpl(AllergyRepository allergyRepository,
                                   AllergenRepository allergenRepository,
                                   VaccineRepository vaccineRepository,
                                   VaccinationRepository vaccinationRepository,
                                   MedicalExaminationRepository medicalExaminationRepository,
                                   MedicalHistoryRepository medicalHistoryRepository,
                                   OperationRepository operationRepository,
                                   HealthRecordRepository healthRecordRepository,
                                   PatientService patientService,
                                   HealthRecordMapper healthRecordMapper) {
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
    public MessageResponse updateHealthrecord(UpdateHealthRecordRequest updateHealthRecordRequest, UUID lbp) {

        // proveri da li su vrednosti koje je korisnik poslao dobre
        BloodType bt = BloodType.forName(updateHealthRecordRequest.getBlodtype());
        //log.info(bt.toString());
        if(bt == null) {
            String errMessage = String.format("Nepoznata krvna grupa '%s'", updateHealthRecordRequest.getBlodtype());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
        RHFactor rhf = RHFactor.valueOfNotation(updateHealthRecordRequest.getRhfactor());
        if (rhf == null) {
            String errMessage = String.format("Nepoznat rh faktor '%s'", updateHealthRecordRequest.getRhfactor());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        // getrecordbylbp
        HealthRecord healthRecord = getRecordByLbp(lbp);

        //update podatke
        healthRecord = healthRecordMapper.updateHealthRecordRequestToHealthRecord(bt,rhf, healthRecord);

        // update podatke u bazi
        healthRecordRepository.save(healthRecord);

        return new MessageResponse("uspesno azurirani podaci zdravstvenog kartona");
    }

    @Override
    public MessageResponse addAllergy(AddAllergyRequest addAllergyRequest, UUID lbp) {

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
        Allergy allergy = healthRecordMapper.addAllergyRequestToVaccinatin(addAllergyRequest, healthRecord, allergen);

        // update podatke u bazi
        allergyRepository.save(allergy);

        return new MessageResponse("uspesno dodata alergija");
    }

    @Override
    public MessageResponse addVaccination(AddVaccinationRequest addVaccinationRequest, UUID lbp) {

        // proveri da li su vrednosti koje je korisnik poslao dobre
        Vaccine vaccine = vaccineRepository.findByName(addVaccinationRequest.getVaccine())
                .orElseThrow(() -> {
                    String errMessage = String.format("vakcina sa nazivom '%s' ne postoji", addVaccinationRequest.getVaccine());
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        // proveri da li je datum manji od trenutnog vremena
        Date vaccinationDate = addVaccinationRequest.getDate();
        if(vaccinationDate.compareTo(new Date(System.currentTimeMillis())) < 0){
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

        // update podatke u bazi
        vaccinationRepository.save(vaccination);

        // update podatke u bazi
        healthRecordRepository.save(healthRecord);

        return new MessageResponse("uspesno dodata vakcinacija");
    }

    @Override
    public VaccineListResponse getAvailableVaccines() {
        List<Vaccine> vaccines = vaccineRepository.findAll();
        return healthRecordMapper.vaccineListToVaccineListResponse(vaccines);
    }

    @Override
    public AllergenListResponse getAvailableAllergens() {
        List<Allergen> vaccines = allergenRepository.findAll();
        return healthRecordMapper.allergenListToVaccineListResponse(vaccines);
    }

}
