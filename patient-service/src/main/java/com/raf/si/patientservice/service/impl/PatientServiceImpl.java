package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.PatientListResponse;
import com.raf.si.patientservice.dto.response.PatientResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.mapper.PatientMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.repository.*;
import com.raf.si.patientservice.repository.filtering.filter.PatientSearchFilter;
import com.raf.si.patientservice.repository.filtering.specification.PatientSpecification;
import com.raf.si.patientservice.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public PatientServiceImpl(PatientRepository patientRepository,
                              HealthRecordRepository healthRecordRepository,
                              VaccinationRepository vaccinationRepository,
                              OperationRepository operationRepository,
                              MedicalHistoryRepository medicalHistoryRepository,
                              MedicalExaminationRepository medicalExaminationRepository,
                              AllergyRepository allergyRepository,
                              PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.operationRepository = operationRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.medicalExaminationRepository = medicalExaminationRepository;
        this.allergyRepository = allergyRepository;
        this.patientMapper = patientMapper;
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
        Patient patient = findPatient(patientRequest.getJmbg());

        patient = patientMapper.patientRequestToPatient(patient, patientRequest);
        patientRepository.save(patient);
        log.info(String.format("Pacijent sa lbp-om '%s' uspesno sacuvan", patient.getLbp()));
        return patientMapper.patientToPatientResponse(patient);
    }

    @Override
    public PatientResponse updatePatientByLbp(PatientRequest patientRequest, UUID lbp) {
        Patient patient = findPatient(lbp);

        patient = patientMapper.patientRequestToPatient(patient, patientRequest);
        patientRepository.save(patient);
        log.info(String.format("Pacijent sa lbp-om '%s' uspesno sacuvan", lbp));
        return patientMapper.patientToPatientResponse(patient);
    }

    @Transactional
    @Override
    public PatientResponse deletePatient(UUID lbp) {
        Patient patient = findPatient(lbp);

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
        return patientMapper.patientToPatientResponse(findPatient(lbp));
    }

    @Override
    public PatientListResponse getPatients(UUID lbp,
                                           String firstName,
                                           String lastName,
                                           String jmbg,
                                           Boolean deleted,
                                           Pageable pageable) {

        PatientSearchFilter patientSearchFilter = new PatientSearchFilter(lbp, firstName, lastName, jmbg, deleted);
        PatientSpecification spec = new PatientSpecification(patientSearchFilter);

        Page<Patient> patientsPage = patientRepository.findAll(spec, pageable);
        return patientMapper.patientPageToPatientListResponse(patientsPage);
    }



    @Override
    public Patient findPatient(UUID lbp){
        return patientRepository.findByLbpAndDeleted(lbp, false)
                .orElseThrow(() -> {
                    String errMessage = String.format("Pacijent sa lbp-om '%s' ne postoji", lbp);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });
    }

    @Override
    public Patient findPatient(String jmbg){
        return patientRepository.findByJmbgAndDeleted(jmbg, false)
                .orElseThrow(() -> {
                    String errMessage = String.format("Pacijent sa jmbg-om '%s' ne postoji", jmbg);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });
    }
}
