package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.PatientResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.mapper.HealthRecordMapper;
import com.raf.si.patientservice.mapper.PatientMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.repository.*;
import com.raf.si.patientservice.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final VaccinationRepository vaccinationRepository;
    private final OperationRepository operationRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final MedicalExaminationRepository medicalExaminationRepository;
    private final AllergyRepository allergyRepository;

    private final PatientMapper patientMapper;
    private final HealthRecordMapper healthRecordMapper;

    public PatientServiceImpl(PatientRepository patientRepository, HealthRecordRepository healthRecordRepository, VaccinationRepository vaccinationRepository, OperationRepository operationRepository, MedicalHistoryRepository medicalHistoryRepository, MedicalExaminationRepository medicalExaminationRepository, AllergyRepository allergyRepository, PatientMapper patientMapper, HealthRecordMapper healthRecordMapper) {
        this.patientRepository = patientRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.operationRepository = operationRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.medicalExaminationRepository = medicalExaminationRepository;
        this.allergyRepository = allergyRepository;
        this.patientMapper = patientMapper;
        this.healthRecordMapper = healthRecordMapper;
    }

    @Transactional
    @Override
    public PatientResponse createPatient(PatientRequest patientRequest) {
        patientRepository.findByJmbg(patientRequest.getJmbg()).ifPresent((k) ->{
            String errMessage = String.format("Pacijent sa jmbg-om '%s' vec postoji", patientRequest.getJmbg());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        });

        Patient patient = patientMapper.patientRequestToPatient(new Patient(), patientRequest);
        HealthRecord healthRecord = new HealthRecord();
        patient.setHealthRecord(healthRecord);

        patientRepository.save(patient);
        healthRecordRepository.save(healthRecord);

        log.info(String.format("Pacijent sa lbp-om '%s' uspesno sacuvan", patient.getLbp()));
        return patientMapper.patientToPatientResponse(patient);
    }

    @Override
    public PatientResponse updatePatientByJmbg(PatientRequest patientRequest) {
        Patient patient = patientRepository.findByJmbgAndDeleted(patientRequest.getJmbg(), false)
                .orElseThrow(() -> {
                    String errMessage = String.format("Pacijent sa jmbg-om '%s' ne postoji", patientRequest.getJmbg());
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
        });

        patient = patientMapper.patientRequestToPatient(patient, patientRequest);
        patientRepository.save(patient);
        log.info(String.format("Pacijent sa lbp-om '%s' uspesno sacuvan", patient.getLbp()));
        return patientMapper.patientToPatientResponse(patient);
    }

    @Override
    public PatientResponse updatePatientByLbp(PatientRequest patientRequest, UUID lbp) {
        Patient patient = patientRepository.findByLbpAndDeleted(lbp, false)
                .orElseThrow(() -> {
                    String errMessage = String.format("Pacijent sa lbp-om '%s' ne postoji", lbp);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        patient = patientMapper.patientRequestToPatient(patient, patientRequest);
        patientRepository.save(patient);
        log.info(String.format("Pacijent sa lbp-om '%s' uspesno sacuvan", lbp));
        return patientMapper.patientToPatientResponse(patient);
    }

    @Transactional
    @Override
    public PatientResponse deletePatient(UUID lbp) {
        Patient patient = patientRepository.findByLbp(lbp)
                .orElseThrow(() -> {
                    String errMessage = String.format("Pacijent sa lbp-om '%s' ne postoji", lbp);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        patient.setDeleted(true);
        patientRepository.save(patient);

        HealthRecord healthRecord = patient.getHealthRecord();
        healthRecord.setDeleted(true);
        healthRecordRepository.save(healthRecord);

        for(Allergy allergy: healthRecord.getAllergies()){
            allergy.setDeleted(true);
            allergyRepository.save(allergy);
        }

        for(MedicalExamination medicalExamination: healthRecord.getMedicalExaminations()){
            medicalExamination.setDeleted(true);
            medicalExaminationRepository.save(medicalExamination);
        }

        for(MedicalHistory medicalHistory: healthRecord.getMedicalHistory()){
            medicalHistory.setDeleted(true);
            medicalHistoryRepository.save(medicalHistory);
        }

        for(Operation operation: healthRecord.getOperations()){
            operation.setDeleted(true);
            operationRepository.save(operation);
        }

        for(Vaccination vaccination: healthRecord.getVaccinations()){
            vaccination.setDeleted(true);
            vaccinationRepository.save(vaccination);
        }

        log.info(String.format("Pacijent sa lbp-om '%s' uspesno obrisan", lbp));
        return patientMapper.patientToPatientResponse(patient);
    }

    @Override
    public PatientResponse getPatientByLbp(UUID lbp) {
        Patient patient = patientRepository.findByLbpAndDeleted(lbp, false)
                .orElseThrow(() -> {
                    String errMessage = String.format("Pacijent sa lbp-om '%s' ne postoji", lbp);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        return patientMapper.patientToPatientResponse(patient);
    }

    @Override
    public HealthRecordResponse getHealthRecordForPatient(UUID lbp) {
        Patient patient = patientRepository.findByLbpAndDeleted(lbp, false)
                .orElseThrow(() -> {
                    String errMessage = String.format("Pacijent sa lbp-om '%s' ne postoji", lbp);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        HealthRecord healthRecord = patient.getHealthRecord();
        HealthRecordResponse response = healthRecordMapper.healthRecordToHealthRecordResponse(patient,
                healthRecord,
                healthRecord.getAllergies(),
                healthRecord.getVaccinations(),
                healthRecord.getMedicalExaminations(),
                healthRecord.getMedicalHistory(),
                healthRecord.getOperations());

        return response;
    }
}
