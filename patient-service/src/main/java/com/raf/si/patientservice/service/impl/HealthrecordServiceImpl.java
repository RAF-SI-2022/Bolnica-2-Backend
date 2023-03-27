package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.AddAllergyRequest;
import com.raf.si.patientservice.dto.request.AddVaccinationRequest;
import com.raf.si.patientservice.dto.request.UpdateHealthRecordRequest;
import com.raf.si.patientservice.dto.response.MessageResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.HealthRecordMapper;
import com.raf.si.patientservice.mapper.PatientMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.repository.*;
import com.raf.si.patientservice.service.HealthrecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class HealthrecordServiceImpl implements HealthrecordService {


    private final PatientRepository patientRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final VaccinationRepository vaccinationRepository;
    private final OperationRepository operationRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final MedicalExaminationRepository medicalExaminationRepository;
    private final AllergyRepository allergyRepository;

    private final PatientMapper patientMapper;
    private final HealthRecordMapper healthRecordMapper;

    private final AllergenRepository allergenRepository;

    private final VaccineRepository vaccineRepository;

    public HealthrecordServiceImpl(PatientRepository patientRepository,
                                   HealthRecordRepository healthRecordRepository,
                                   VaccinationRepository vaccinationRepository,
                                   OperationRepository operationRepository,
                                   MedicalHistoryRepository medicalHistoryRepository,
                                   MedicalExaminationRepository medicalExaminationRepository,
                                   AllergyRepository allergyRepository,
                                   PatientMapper patientMapper,
                                   HealthRecordMapper healthRecordMapper,
                                   AllergenRepository allergenRepository,
                                   VaccineRepository vaccineRepository) {
        this.patientRepository = patientRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.operationRepository = operationRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.medicalExaminationRepository = medicalExaminationRepository;
        this.allergyRepository = allergyRepository;
        this.patientMapper = patientMapper;
        this.healthRecordMapper = healthRecordMapper;
        this.allergenRepository = allergenRepository;
        this.vaccineRepository = vaccineRepository;
    }


    private HealthRecord getRecordByLbp(UUID lbp) {
        // dohvati iz baze korisnika
        Optional<Patient> patient = patientRepository.findByLbp(lbp);
        if(patient.isEmpty()) {
            String errMessage = String.format("Pacijent sa lbp '%s' ne postoji", lbp);
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
        HealthRecord healthRecord = patient.get().getHealthRecord();
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
        BloodType bt = BloodType.valueOfNotation(updateHealthRecordRequest.getBlodtype());
        if(bt == null) {
            String errMessage = String.format("Nepoznata krvna grupa '%s'", updateHealthRecordRequest.getBlodtype());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
        RHFactor rhf = RHFactor.valueOfNotation(updateHealthRecordRequest.getRhfactor());
        if (rhf == null) {
            String errMessage = String.format("Nepoznat rh faktor '%s'", updateHealthRecordRequest.getBlodtype());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        // getrecordbylbp
        HealthRecord healthRecord = getRecordByLbp(lbp);

        // TODO: proveriti da li ce update promeniti nesto

        //update podatke
        healthRecord.setBloodType(bt);
        healthRecord.setRhFactor(rhf);

        // update podatke u bazi
        healthRecordRepository.save(healthRecord);

        return null;
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
        Allergy allergy = new Allergy();
        allergy.setAllergen(allergen);
        allergy.setDeleted(false);
        allergy.setHealthRecord(healthRecord);
        // TODO da li treba update healthrecord-a
        //healthRecord.getAllergies().add(allergy);

        // update podatke u bazi
        allergyRepository.save(allergy);
        // TODO da li treba update healthrecord-a
        //healthRecordRepository.save(healthRecord);

        return null;
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

        // TODO ???proveriti da li je vec vakcinisan tom vakcinom???

        // getrecordbylbp
        HealthRecord healthRecord = getRecordByLbp(lbp);


        // proveriti da li je vakcinacija tokom lifetime-a pacijenta
        Optional<Patient> patient = patientRepository.findByLbp(lbp);
        if(patient.isEmpty()) {
            String errMessage = String.format("Pacijent sa lbp '%s' ne postoji", lbp);
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
        if(vaccinationDate.compareTo(patient.get().getBirthDate()) < 0
            ||
            (
                patient.get().getDeathDate() != null
                &&
                vaccinationDate.compareTo(patient.get().getDeathDate())>0
            )) {
            String errMessage = String.format("datum vakcinacije mora biti izmedju rodjenja i smrti pacijenta");
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }



        //update podatke
        Vaccination vaccination = new Vaccination();
        vaccination.setVaccine(vaccine);
        vaccination.setDeleted(false);
        vaccination.setVaccinationDate(vaccinationDate);
        vaccination.setHealthRecord(healthRecord);
        // TODO da li treba update healthrecord-a
        //healthRecord.getVaccinations().add(vaccination);

        // update podatke u bazi
        vaccinationRepository.save(vaccination);
        // TODO da li treba update healthrecord-a
        //healthRecordRepository.save(healthRecord);

        // update podatke u bazi
        healthRecordRepository.save(healthRecord);

        return null;
    }
}
