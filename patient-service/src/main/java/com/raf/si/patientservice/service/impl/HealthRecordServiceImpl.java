package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.DateBetweenRequest;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.LightHealthRecordResponse;
import com.raf.si.patientservice.dto.response.MedicalExaminationListResponse;
import com.raf.si.patientservice.dto.response.MedicalHistoryListResponse;
import com.raf.si.patientservice.mapper.HealthRecordMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.repository.*;
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
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    private static final String PERMITTED_DOC = "ROLE_DR_SPEC_POV";

    private final AllergyRepository allergyRepository;
    private final VaccinationRepository vaccinationRepository;
    private final MedicalExaminationRepository medicalExaminationRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final OperationRepository operationRepository;

    private final PatientService patientService;

    private final HealthRecordMapper healthRecordMapper;


    public HealthRecordServiceImpl(HealthRecordRepository healthRecordRepository,
                                   AllergyRepository allergyRepository,
                                   VaccinationRepository vaccinationRepository,
                                   MedicalExaminationRepository medicalExaminationRepository,
                                   MedicalHistoryRepository medicalHistoryRepository,
                                   OperationRepository operationRepository,
                                   PatientService patientService,
                                   HealthRecordMapper healthRecordMapper) {
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

        Page<MedicalExamination> examinations;
        Page<MedicalHistory> medicalHistory;

        if(canGetConfidential()){
            medicalHistory = medicalHistoryRepository.findByHealthRecord(healthRecord, pageable);
             examinations = medicalExaminationRepository.findByHealthRecord(healthRecord, pageable);
        }else{
            medicalHistory = medicalHistoryRepository.findByHealthRecordAndConfidential(healthRecord, false, pageable);
            examinations = medicalExaminationRepository.findByHealthRecordAndConfidential(healthRecord, false, pageable);
        }

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
        Page<MedicalExamination> examinations;

        if(request.getStartDate() == null) {
            if(canGetConfidential())
                examinations = medicalExaminationRepository.findByHealthRecord(healthRecord, pageable);
            else
                examinations = medicalExaminationRepository.findByHealthRecordAndConfidential(healthRecord, false, pageable);
        }else {
            Date startDate = DateUtils.truncate(request.getStartDate(), Calendar.DAY_OF_MONTH);
            Date endDate = request.getEndDate() == null ? DateUtils.addDays(startDate, 1) :
                    DateUtils.truncate(request.getEndDate(), Calendar.DAY_OF_MONTH);

            if(canGetConfidential())
                examinations = medicalExaminationRepository.findByHealthRecordAndDateBetween(healthRecord,
                        startDate,
                        endDate,
                        pageable);
            else
                examinations = medicalExaminationRepository.findByHealthRecordAndConfidentialAndDateBetween(healthRecord,
                        false,
                        startDate,
                        endDate,
                        pageable);
        }

        return healthRecordMapper.getPermittedExaminations(examinations);
    }

    @Override
    public MedicalHistoryListResponse findMedicalHistory(UUID lbp, String diagnosisCode, Pageable pageable) {
        Patient patient = patientService.findPatient(lbp);
        HealthRecord healthRecord = patient.getHealthRecord();
        Page<MedicalHistory> medicalHistory;

        if(diagnosisCode == null || diagnosisCode.trim().isEmpty()) {
            if(canGetConfidential())
                medicalHistory = medicalHistoryRepository.findByHealthRecord(healthRecord, pageable);
            else
                medicalHistory = medicalHistoryRepository.findByHealthRecordAndConfidential(healthRecord, false, pageable);
        }
        else {
            if(canGetConfidential())
                medicalHistory = medicalHistoryRepository.findByHealthRecordAndDiagnosis_code(healthRecord, diagnosisCode, pageable);
            else
                medicalHistory = medicalHistoryRepository.findByHealthRecordAndConfidentialAndDiagnosis_code(healthRecord,
                        false,
                        diagnosisCode,
                        pageable);
        }

        return healthRecordMapper.getPermittedMedicalHistory(medicalHistory);
    }



    private boolean canGetConfidential(){
        return TokenPayloadUtil.getTokenPayload()
                .getPermissions()
                .stream()
                .anyMatch(permission -> permission.equals(PERMITTED_DOC));
    }
}
