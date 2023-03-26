package com.raf.si.patientservice.bootstrap;


import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.model.enums.medicalhistory.TreatmentResult;
import com.raf.si.patientservice.model.enums.patient.CountryCode;
import com.raf.si.patientservice.model.enums.patient.Gender;
import com.raf.si.patientservice.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class BootstrapData implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final VaccinationRepository vaccinationRepository;
    private final OperationRepository operationRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final MedicalExaminationRepository medicalExaminationRepository;
    private final AllergyRepository allergyRepository;
    private final DiagnosisRepository diagnosisRepository;

    private final AllergenRepository allergenRepository;
    private final VaccineRepository vaccineRepository;

    public BootstrapData(PatientRepository patientRepository,
                         HealthRecordRepository healthRecordRepository,
                         VaccinationRepository vaccinationRepository,
                         OperationRepository operationRepository,
                         MedicalHistoryRepository medicalHistoryRepository,
                         MedicalExaminationRepository medicalExaminationRepository,
                         AllergyRepository allergyRepository,
                         DiagnosisRepository diagnosisRepository,
                         AllergenRepository allergenRepository,
                         VaccineRepository vaccineRepository) {

        this.patientRepository = patientRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.operationRepository = operationRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.medicalExaminationRepository = medicalExaminationRepository;
        this.allergyRepository = allergyRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.allergenRepository = allergenRepository;
        this.vaccineRepository = vaccineRepository;
    }

    @Override
    public void run(String... args) {
        makePatient();
    }

    private void makePatient(){
        Patient patient = new Patient();
        patient.setJmbg("1342002345612");
        patient.setFirstName("Pacijent");
        patient.setLastName("Pacijentovic");
        patient.setParentName("Roditelj");
        patient.setGender(Gender.MUSKI);
        patient.setBirthDate(new Date());
        patient.setBirthplace("Resnjak");
        patient.setCitizenshipCountry(CountryCode.SRB);
        patient.setCountryOfLiving(CountryCode.AFG);
        patient.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));


        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setRegistrationDate(new Date());
        healthRecord.setBloodType(BloodType.A);
        healthRecord.setRhFactor(RHFactor.PLUS);


        Operation operation = new Operation();
        operation.setDate(new Date());
        operation.setDescription("Operacija");
        operation.setPbo(UUID.randomUUID());

        operation.setHealthRecord(healthRecord);


        MedicalExamination examination = new MedicalExamination();
        examination.setLbz(UUID.randomUUID());
        examination.setDate(new Date());
        examination.setObjectiveFinding("Nadjeno bla bla");

        examination.setHealthRecord(healthRecord);

        MedicalExamination examination2 = new MedicalExamination();
        examination2.setLbz(UUID.randomUUID());
        examination2.setDate(new Date());
        examination2.setObjectiveFinding("Nadjeno bla bla 2");
        examination2.setConfidential(true);

        examination2.setHealthRecord(healthRecord);


        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("AAA");
        diagnosis.setDescription("Opis");
        diagnosis.setLatinDescription("Latinski opis");
        diagnosisRepository.save(diagnosis);

        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setDiagnosis(diagnosis);
        medicalHistory.setIllnessStart(new Date());
        medicalHistory.setTreatmentResult(TreatmentResult.U_TOKU);
        medicalHistory.setCurrentStateDescription("Trenutno stanje");
        medicalHistory.setValid(true);
        medicalHistory.setValidFrom(new Date());
        medicalHistory.setValidUntil(new Date());

        medicalHistory.setHealthRecord(healthRecord);


        patient.setHealthRecord(healthRecord);
        patientRepository.save(patient);
        healthRecordRepository.save(healthRecord);
        operationRepository.save(operation);
        medicalExaminationRepository.save(examination);
        medicalExaminationRepository.save(examination2);
        medicalHistoryRepository.save(medicalHistory);


        Allergen allergen = new Allergen();
        allergen.setName("alergija");

        allergenRepository.save(allergen);

        Allergy allergy = new Allergy();
        allergy.setHealthRecord(healthRecord);
        allergy.setAllergen(allergen);

        allergyRepository.save(allergy);

        Allergen allergen2 = new Allergen();
        allergen2.setName("alergija 2");

        allergenRepository.save(allergen2);

        Allergy allergy2 = new Allergy();
        allergy2.setHealthRecord(healthRecord);
        allergy2.setAllergen(allergen2);

        allergyRepository.save(allergy2);


        Vaccine vaccine = new Vaccine();
        vaccine.setName("PRIORIX");
        vaccine.setType("Virusne vakcine");
        vaccine.setDescription("Vakcina protiv malih boginja");

        vaccineRepository.save(vaccine);

        Vaccination vaccination = new Vaccination();
        vaccination.setHealthRecord(healthRecord);
        vaccination.setVaccine(vaccine);
        vaccination.setVaccinationDate(new Date());

        vaccinationRepository.save(vaccination);
    }
}