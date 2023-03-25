package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.MedicalExaminationRequest;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.LightHealthRecordResponse;
import com.raf.si.patientservice.dto.response.MedicalExaminationListResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.mapper.HealthRecordMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.repository.*;
import com.raf.si.patientservice.service.HealthRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    private final PatientRepository patientRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final AllergyRepository allergyRepository;
    private final VaccinationRepository vaccinationRepository;
    private final MedicalExaminationRepository medicalExaminationRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final OperationRepository operationRepository;
    private final HealthRecordMapper healthRecordMapper;

    public HealthRecordServiceImpl(PatientRepository patientRepository, HealthRecordRepository healthRecordRepository, AllergyRepository allergyRepository, VaccinationRepository vaccinationRepository, MedicalExaminationRepository medicalExaminationRepository, MedicalHistoryRepository medicalHistoryRepository, OperationRepository operationRepository, HealthRecordMapper healthRecordMapper) {
        this.patientRepository = patientRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.allergyRepository = allergyRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.medicalExaminationRepository = medicalExaminationRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.operationRepository = operationRepository;
        this.healthRecordMapper = healthRecordMapper;
    }

    @Override
    public HealthRecordResponse getHealthRecordForPatient(UUID lbp, Pageable pageable) {
        Patient patient = patientRepository.findByLbpAndDeleted(lbp, false)
                .orElseThrow(() -> {
                    String errMessage = String.format("Pacijent sa lbp-om '%s' ne postoji", lbp);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        HealthRecord healthRecord = patient.getHealthRecord();
        List<Allergy> allergies = allergyRepository.findByHealthRecord(healthRecord, pageable);
        List<Vaccination> vaccinations = vaccinationRepository.findByHealthRecord(healthRecord, pageable);
        List<MedicalExamination> examinations = medicalExaminationRepository.findByHealthRecord(healthRecord, pageable);
        List<MedicalHistory> medicalHistory = medicalHistoryRepository.findByHealthRecord(healthRecord, pageable);
        List<Operation> operations = operationRepository.findByHealthRecord(healthRecord, pageable);

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
        Patient patient = patientRepository.findByLbpAndDeleted(lbp, false)
                .orElseThrow(() -> {
                    String errMessage = String.format("Pacijent sa lbp-om '%s' ne postoji", lbp);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        HealthRecord healthRecord = patient.getHealthRecord();
        List<Allergy> allergies = allergyRepository.findByHealthRecord(healthRecord, pageable);
        List<Vaccination> vaccinations = vaccinationRepository.findByHealthRecord(healthRecord, pageable);

        return healthRecordMapper.healthRecordToLightHealthRecordResponse(patient,
                healthRecord,
                allergies,
                vaccinations);
    }

    @Override
    public MedicalExaminationListResponse findExaminations(MedicalExaminationRequest request, Pageable pageable) {
        Patient patient = patientRepository.findByLbpAndDeleted(request.getLbp(), false)
                .orElseThrow(() -> {
                    String errMessage = String.format("Pacijent sa lbp-om '%s' ne postoji", request.getLbp());
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        HealthRecord healthRecord = patient.getHealthRecord();
        List<MedicalExamination> examinations;

        if(request.getStartDate() == null) {
            examinations = medicalExaminationRepository.findByHealthRecord(healthRecord, pageable);
        }else {
            Date startDate = DateUtils.truncate(request.getStartDate(), Calendar.DAY_OF_MONTH);
            Date endDate = request.getEndDate() == null ? DateUtils.addDays(startDate, 1) :
                    DateUtils.truncate(request.getEndDate(), Calendar.DAY_OF_MONTH);

            examinations = medicalExaminationRepository.findByHealthRecordAndDateBetween(healthRecord, startDate, endDate, pageable);
        }

        return new MedicalExaminationListResponse(healthRecordMapper.getPermittedExaminations(examinations));
    }
}
