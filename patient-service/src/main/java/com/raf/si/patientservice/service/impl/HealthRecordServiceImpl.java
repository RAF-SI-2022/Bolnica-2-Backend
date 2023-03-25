package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.mapper.HealthRecordMapper;
import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.HealthRecordRepository;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.service.HealthRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    private final PatientRepository patientRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final HealthRecordMapper healthRecordMapper;

    public HealthRecordServiceImpl(PatientRepository patientRepository, HealthRecordRepository healthRecordRepository, HealthRecordMapper healthRecordMapper) {
        this.patientRepository = patientRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.healthRecordMapper = healthRecordMapper;
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
