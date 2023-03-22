package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.PatientResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.mapper.PatientMapper;
import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.HealthRecordRepository;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final PatientMapper patientMapper;

    public PatientServiceImpl(PatientRepository patientRepository, HealthRecordRepository healthRecordRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.healthRecordRepository = healthRecordRepository;
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
        patientRepository.save(patient);

        HealthRecord healthRecord = new HealthRecord();
        patient.setHealthRecord(healthRecord);
        healthRecordRepository.save(healthRecord);

        log.info(String.format("Pacijent sa lbp-om '%s' uspesno sacuvan", patient.getLbp()));
        return patientMapper.patientToPatientResponse(patient);
    }

    @Override
    public PatientResponse updatePatient(PatientRequest patientRequest) {
        Patient patient = patientRepository.findByJmbg(patientRequest.getJmbg()).orElseThrow(() -> {
            String errMessage = String.format("Pacijent sa jmbg-om '%s' ne postoji", patientRequest.getJmbg());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        });

        patient = patientMapper.patientRequestToPatient(patient, patientRequest);
        patientRepository.save(patient);
        log.info(String.format("Pacijent sa lbp-om '%s' uspesno sacuvan", patient.getLbp()));
        return patientMapper.patientToPatientResponse(patient);
    }
}
