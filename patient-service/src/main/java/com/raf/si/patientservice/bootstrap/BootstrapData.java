package com.raf.si.patientservice.bootstrap;


import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.model.enums.medicalhistory.TreatmentResult;
import com.raf.si.patientservice.model.enums.patient.*;
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
    private final ScheduledMedExamRepository scheduledMedExamRepository;

    public BootstrapData(PatientRepository patientRepository,
                         HealthRecordRepository healthRecordRepository,
                         VaccinationRepository vaccinationRepository,
                         OperationRepository operationRepository,
                         MedicalHistoryRepository medicalHistoryRepository,
                         MedicalExaminationRepository medicalExaminationRepository,
                         AllergyRepository allergyRepository,
                         DiagnosisRepository diagnosisRepository,
                         AllergenRepository allergenRepository,
                         VaccineRepository vaccineRepository, ScheduledMedExamRepository scheduledMedExamRepository) {

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
        this.scheduledMedExamRepository = scheduledMedExamRepository;
    }

    @Override
    public void run(String... args) {
        makePatient();
        makeSchedExam();
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

        patient.setAddress("Jurija Gagarina 16");
        patient.setPlaceOfLiving("Novi Beograd");
        patient.setPhoneNumber("0601234567");
        patient.setEmail("pacijent.pacijentovic@gmail.com");
        patient.setCustodianJmbg("0101987123456");
        patient.setCustodianName("Staratelj Starateljovic");
        patient.setFamilyStatus(FamilyStatus.OBA_RODITELJA);
        patient.setMaritalStatus(MaritalStatus.SAMAC);
        patient.setChildrenNum(0);
        patient.setEducation(Education.VISOKO_OBRAZOVANJE);
        patient.setProfession("Programer");


        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setRegistrationDate(new Date());
        healthRecord.setBloodType(BloodType.A);
        healthRecord.setRhFactor(RHFactor.PLUS);


        Operation operation = new Operation();
        operation.setDate(new Date());
        operation.setDescription("Operacija");
        operation.setPbo(UUID.randomUUID());

        operation.setHealthRecord(healthRecord);


        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode("A15.3");
        diagnosis.setDescription("Grip, virus nedokazan");
        diagnosis.setLatinDescription("Influenza, virus non identificatum");
        diagnosisRepository.save(diagnosis);

        MedicalExamination examination = new MedicalExamination();
        examination.setLbz(UUID.randomUUID());
        examination.setDate(new Date());
        examination.setObjectiveFinding("Grip, ne zna se koji virus je uzrok");
        examination.setMainSymptoms("Oteceno grlo, temperatura");
        examination.setCurrentIllness("Prehlada");
        examination.setAnamnesis("Bol u grlu, temperatura, pacijentu je konstantno hladno");
        examination.setFamilyAnamnesis("Bol u grlu, temperatura");
        examination.setPatientOpinion("Streptokoke");
        examination.setDiagnosis(diagnosis);
        examination.setSuggestedTherapy("Odmor, septolete jednom dnevno");
        examination.setAdvice("Odmor");

        examination.setHealthRecord(healthRecord);

        MedicalExamination examination2 = new MedicalExamination();
        examination2.setLbz(UUID.randomUUID());
        examination2.setDate(new Date());
        examination2.setObjectiveFinding("Male boginje po celom telu");
        examination2.setConfidential(true);

        examination2.setHealthRecord(healthRecord);

        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setDiagnosis(diagnosis);
        medicalHistory.setIllnessStart(new Date());
        medicalHistory.setTreatmentResult(TreatmentResult.U_TOKU);
        medicalHistory.setCurrentStateDescription("Trenutno stanje");
        medicalHistory.setValid(true);
        medicalHistory.setValidFrom(new Date());
        medicalHistory.setValidUntil(new Date());
        medicalHistory.setConfidential(true);

        medicalHistory.setHealthRecord(healthRecord);


        patient.setHealthRecord(healthRecord);
        patientRepository.save(patient);
        healthRecordRepository.save(healthRecord);
        operationRepository.save(operation);
        medicalExaminationRepository.save(examination);
        medicalExaminationRepository.save(examination2);
        medicalHistoryRepository.save(medicalHistory);


        // dodaj sve alergije
        String[] allergenNames = {"mleko", "jaja", "orasasti plodovi", "plodovi mora", "psenica",
                        "soja", "riba", "penicilin"};
        for(int i=0;i<allergenNames.length; i+=1) {
            Allergen allergen= new Allergen();
            allergen.setName(allergenNames[i]);
            allergenRepository.save(allergen);

            if(i<=1){
                // dodaj jednu alergiju
                Allergy allergy = new Allergy();
                allergy.setHealthRecord(healthRecord);
                allergy.setAllergen(allergen);
                allergyRepository.save(allergy);
            }
        }

        // dodaj sve vakcine
        String[] vaccineNames = {"PRIORIX", "HIBERIX", "INFLUVAC", "SYNFLORIX", "BCG VAKCINA"};
        String[] vaccineType = {"Virusne vakcine", "Bakterijske vakcine","Virusne vakcine", "Bakterijske vakcine", "Bakterijske vakcine"};
        String[] vaccineDescription = {
                "Vakcina protiv morbila (malih boginja)",
                "Kapsulirani antigen hemofilus influence tip B",
                "Virusne vakcine protiv influence (grip)",
                "Vakcine protiv pneumokoka",
                "Vakcine protiv tuberkuloze"};
        String[] vaccineProducer = {
                "GlaxoSmithKline Biologicals S.A., Belgija",
                "GlaxoSmithKline Biologicals S.A., Belgija",
                "Abbott Biologicals B.V., Holandija",
                "GlaxoSmithKline Biologicals S.A., Belgija",
                "Institut za virusologiju, vakcine i serume \"Torlak\", Republika Srbija"};

        for(int i=0;i<vaccineNames.length;i+=1) {
            Vaccine vaccine = new Vaccine();
            vaccine.setName(vaccineNames[i]);
            vaccine.setType(vaccineType[i]);
            vaccine.setDescription(vaccineDescription[i]);
            vaccine.setProducer(vaccineProducer[i]);
            vaccineRepository.save(vaccine);

            if(i<=1){
                // dodaj prve dve vakcine kao vakcinacije u karton
                Vaccination vaccination = new Vaccination();
                vaccination.setHealthRecord(healthRecord);
                vaccination.setVaccine(vaccine);
                vaccination.setVaccinationDate(new Date());
                vaccinationRepository.save(vaccination);
            }
        }
    }

    private  void makeSchedExam(){
        ScheduledMedExamination scheduledMedExamination= new ScheduledMedExamination();
        scheduledMedExamination.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        //                                                          5a2e71bb-e4ee-43dd-a3ad-28e043f8b435
        scheduledMedExamination.setLbzDoctor(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        scheduledMedExamination.setAppointmentDate(new Date());
        scheduledMedExamination.setNote("Pacijent ima bol u zuci");
        scheduledMedExamination.setLbzNurse(UUID.fromString("5a2e71bb-e4ee-43dd-55a3-28e043f8b435"));

        scheduledMedExamRepository.save(scheduledMedExamination);
    }
}