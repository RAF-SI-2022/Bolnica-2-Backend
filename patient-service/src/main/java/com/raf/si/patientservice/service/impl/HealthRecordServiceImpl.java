package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.DateBetweenRequest;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.LightHealthRecordResponse;
import com.raf.si.patientservice.dto.response.MedicalExaminationListResponse;
import com.raf.si.patientservice.dto.response.MedicalHistoryListResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    private static final String PERMITTED_DOC = "ROLE_DR_SPEC_POV";

    private final HealthRecordRepository healthRecordRepository;
    private final AllergyRepository allergyRepository;
    private final VaccinationRepository vaccinationRepository;
    private final MedicalExaminationRepository medicalExaminationRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final OperationRepository operationRepository;

    private final PatientService patientService;

    private final HealthRecordMapper healthRecordMapper;


    public HealthRecordServiceImpl(HealthRecordRepository healthRecordRepository,
                                   HealthRecordRepository healthRecordRepository1, AllergyRepository allergyRepository,
                                   VaccinationRepository vaccinationRepository,
                                   MedicalExaminationRepository medicalExaminationRepository,
                                   MedicalHistoryRepository medicalHistoryRepository,
                                   OperationRepository operationRepository,
                                   PatientService patientService,
                                   HealthRecordMapper healthRecordMapper) {
        this.healthRecordRepository = healthRecordRepository1;
        this.allergyRepository = allergyRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.medicalExaminationRepository = medicalExaminationRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.operationRepository = operationRepository;
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
    public MedicalExaminationListResponse findExaminations(UUID lbp, DateBetweenRequest request, Pageable pageable) {
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
}
