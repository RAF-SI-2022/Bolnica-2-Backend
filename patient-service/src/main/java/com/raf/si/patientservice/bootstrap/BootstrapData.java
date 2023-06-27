package com.raf.si.patientservice.bootstrap;


import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.appointment.AppointmentStatus;
import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.model.enums.medicalhistory.TreatmentResult;
import com.raf.si.patientservice.model.enums.patient.*;
import com.raf.si.patientservice.repository.*;
import org.hibernate.Session;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final HospitalRoomRepository hospitalRoomRepository;
    private final HospitalizationRepository hospitalizationRepository;
    private final AppointmentRepository appointmentRepository;
    private final ScheduledVaccinationCovidRepository scheduledVaccinationCovidRepository;
    private final VaccinationCovidRepository vaccinationCovidRepository;
    private final AvailableTermRepository availableTermRepository;

    public BootstrapData(PatientRepository patientRepository,
                         HealthRecordRepository healthRecordRepository,
                         VaccinationRepository vaccinationRepository,
                         OperationRepository operationRepository,
                         MedicalHistoryRepository medicalHistoryRepository,
                         MedicalExaminationRepository medicalExaminationRepository,
                         AllergyRepository allergyRepository,
                         DiagnosisRepository diagnosisRepository,
                         AllergenRepository allergenRepository,
                         VaccineRepository vaccineRepository,
                         ScheduledMedExamRepository scheduledMedExamRepository,
                         HospitalRoomRepository hospitalRoomRepository,
                         HospitalizationRepository hospitalizationRepository,
                         AppointmentRepository appointmentRepository,
                         ScheduledVaccinationCovidRepository scheduledVaccinationCovidRepository,
                         VaccinationCovidRepository vaccinationCovidRepository,
                         AvailableTermRepository availableTermRepository) {

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
        this.hospitalRoomRepository = hospitalRoomRepository;
        this.hospitalizationRepository = hospitalizationRepository;
        this.appointmentRepository = appointmentRepository;
        this.vaccinationCovidRepository = vaccinationCovidRepository;
        this.scheduledVaccinationCovidRepository = scheduledVaccinationCovidRepository;
        this.availableTermRepository = availableTermRepository;
    }

    @Override
    public void run(String... args) throws ParseException, IOException {
        makePatient();
        HospitalRoom hospitalRoom = makeRoom();
        addCovidRooms();
        makeHospitalization(hospitalRoom);
        makeAppointment();
        //makeSchedExam();
    }

    private void addCovidRooms() {
        HospitalRoom hospitalRoom = new HospitalRoom();
        hospitalRoom.setPbo(UUID.fromString("50869452-02f6-4ef7-8592-24d342cd70d1"));
        hospitalRoom.setDescription("COVID Bolnička soba 1");
        hospitalRoom.setRoomName("COVID Soba 1");
        hospitalRoom.setRoomNumber(1);
        hospitalRoom.setCapacity(15);
        hospitalRoom.setOccupation(1);
        hospitalRoom.setCovid(true);
        hospitalRoom.setRespirators(5);
        hospitalRoomRepository.save(hospitalRoom);

        HospitalRoom hospitalRoom1 = new HospitalRoom();
        hospitalRoom1.setPbo(UUID.fromString("50869452-02f6-4ef7-8592-24d342cd70d1"));
        hospitalRoom1.setDescription("Bolnička soba 2");
        hospitalRoom1.setRoomName("Soba 2");
        hospitalRoom1.setRoomNumber(2);
        hospitalRoom1.setCapacity(20);
        hospitalRoom1.setOccupation(1);
        hospitalRoom1.setRespirators(5);
        hospitalRoomRepository.save(hospitalRoom1);

        HospitalRoom hospitalRoom2 = new HospitalRoom();
        hospitalRoom2.setPbo(UUID.fromString("50869452-02f6-4ef7-8592-24d342cd70d1"));
        hospitalRoom2.setDescription("COVID Bolnička soba 3");
        hospitalRoom2.setRoomName("COVID Soba 3");
        hospitalRoom2.setRoomNumber(3);
        hospitalRoom2.setCapacity(15);
        hospitalRoom2.setOccupation(1);
        hospitalRoom2.setCovid(true);
        hospitalRoom2.setRespirators(5);
        hospitalRoomRepository.save(hospitalRoom2);
    }

    private void makePatient() throws ParseException, IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Patient patient = new Patient();
        patient.setJmbg("1342002345612");
        patient.setFirstName("Nemanja");
        patient.setLastName("Todorović");
        patient.setParentName("Žarko");
        patient.setGender(Gender.MUSKI);
        patient.setBirthDate(formatter.parse("07/01/2000"));
        patient.setBirthplace("Resnjak");
        patient.setCitizenshipCountry(CountryCode.SRB);
        patient.setCountryOfLiving(CountryCode.AFG);
        patient.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));

        patient.setAddress("Jurija Gagarina 16");
        patient.setPlaceOfLiving("Novi Beograd");
        patient.setPhoneNumber("0601234567");
        patient.setEmail("mceculovic2819rn@raf.rs");
        patient.setCustodianJmbg("0101987123456");
        patient.setCustodianName("Žarko Todorović");
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


        Diagnosis diagnosis1 = new Diagnosis();
        diagnosis1.setCode("G40. 909");
        diagnosis1.setDescription("Smrtonosna bolest mozga");
        diagnosis1.setLatinDescription("Influenza, virus non identificatum");
        diagnosisRepository.save(diagnosis1);

        MedicalExamination examination = new MedicalExamination();
        examination.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        examination.setDate(formatter.parse("21/12/2022"));
        examination.setObjectiveFinding("Grip, ne zna se koji virus je uzrok");
        examination.setMainSymptoms("Oteceno grlo, temperatura");
        examination.setCurrentIllness("Prehlada");
        examination.setAnamnesis("Bol u grlu, temperatura, pacijentu je konstantno hladno");
        examination.setFamilyAnamnesis("Bol u grlu, temperatura");
        examination.setPatientOpinion("Streptokoke");
        examination.setDiagnosis(diagnosis1);
        examination.setSuggestedTherapy("Odmor, septolete jednom dnevno");
        examination.setAdvice("Odmor");

        examination.setHealthRecord(healthRecord);

        MedicalExamination examination2 = new MedicalExamination();
        examination2.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        examination2.setDate(formatter.parse("21/01/2022"));
        examination2.setObjectiveFinding("Male boginje po celom telu");
        examination2.setConfidential(true);

        examination2.setHealthRecord(healthRecord);

        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setDiagnosis(diagnosis1);
        medicalHistory.setIllnessStart(formatter.parse("16/12/2022"));
        medicalHistory.setTreatmentResult(TreatmentResult.U_TOKU);
        medicalHistory.setCurrentStateDescription("U teškom stanju");
        medicalHistory.setValid(true);
        medicalHistory.setValidFrom(formatter.parse("21/12/2022"));
        medicalHistory.setValidUntil(formatter.parse("03/12/2023"));
        medicalHistory.setConfidential(true);

        medicalHistory.setHealthRecord(healthRecord);


        patient.setHealthRecord(healthRecord);
        patientRepository.save(patient);
        healthRecordRepository.save(healthRecord);
        operationRepository.save(operation);
        medicalExaminationRepository.save(examination);
        medicalExaminationRepository.save(examination2);
        medicalHistoryRepository.save(medicalHistory);

        //makeVaccCovidAndSchedVacc(List.of(patient));
        // dodaj sve alergije
        String[] allergenNames = {"mleko", "jaja", "orasasti plodovi", "plodovi mora", "psenica",
                "soja", "riba", "penicilin"};
        for (int i = 0; i < allergenNames.length; i += 1) {
            Allergen allergen = new Allergen();
            allergen.setName(allergenNames[i]);
            allergenRepository.save(allergen);

            if (i <= 1) {
                // dodaj jednu alergiju
                Allergy allergy = new Allergy();
                allergy.setHealthRecord(healthRecord);
                allergy.setAllergen(allergen);
                allergyRepository.save(allergy);
            }
        }

        // dodaj sve vakcine
        String[] vaccineNames = {"PRIORIX", "HIBERIX", "INFLUVAC", "SYNFLORIX", "BCG VAKCINA"};
        String[] vaccineType = {"Virusne vakcine", "Bakterijske vakcine", "Virusne vakcine", "Bakterijske vakcine", "Bakterijske vakcine"};
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

        for (int i = 0; i < vaccineNames.length; i += 1) {
            Vaccine vaccine = new Vaccine();
            vaccine.setName(vaccineNames[i]);
            vaccine.setType(vaccineType[i]);
            vaccine.setDescription(vaccineDescription[i]);
            vaccine.setProducer(vaccineProducer[i]);
            vaccineRepository.save(vaccine);

            if (i <= 1) {
                // dodaj prve dve vakcine kao vakcinacije u karton
                Vaccination vaccination = new Vaccination();
                vaccination.setHealthRecord(healthRecord);
                vaccination.setVaccine(vaccine);
                vaccination.setVaccinationDate(new Date());
                vaccinationRepository.save(vaccination);
            }
        }

        Vaccine vaccine = new Vaccine();
        vaccine.setName("Astrazeneka");
        vaccine.setType("COVID vakcina");
        vaccine.setDescription("COVID vakcina");
        vaccine.setProducer("Astrazeneka");
        vaccineRepository.save(vaccine);

        Vaccine vaccine1 = new Vaccine();
        vaccine1.setName("Sinopharm");
        vaccine1.setType("COVID vakcina");
        vaccine1.setDescription("COVID vakcina");
        vaccine1.setProducer("Sinopharm");
        vaccineRepository.save(vaccine1);

        Vaccine vaccine2 = new Vaccine();
        vaccine2.setName("Pfizer");
        vaccine2.setType("COVID vakcina");
        vaccine2.setDescription("COVID vakcina");
        vaccine2.setProducer("Pfizer");
        vaccineRepository.save(vaccine2);

        Vaccine vaccine3 = new Vaccine();
        vaccine3.setName("Sputnik V");
        vaccine3.setType("COVID vakcina");
        vaccine3.setDescription("COVID vakcina");
        vaccine3.setProducer("Sputnik V");
        vaccineRepository.save(vaccine3);

        Vaccine vaccine4 = new Vaccine();
        vaccine4.setName("Moderna");
        vaccine4.setType("COVID vakcina");
        vaccine4.setDescription("COVID vakcina");
        vaccine4.setProducer("Moderna");
        vaccineRepository.save(vaccine4);


        //dodaj sve dijagnoze
        String[] mkb10 = {"A15.3", "D50", "I10", "I35.0", "J11", "J12.9", "K35", "K70.3", "K71.0", "N20.0"};
        String[] description =
                {
                        "Tuberkuloza pluća, potvrđena neoznačenim metodama",
                        "Anemija uzrokovana nedostatkom gvožđa",
                        "Povišen krvni pritisak, nepoznatog porekla",
                        "Suženje aortnog zaliska",
                        "Grip, virus nedokazan",
                        "Zapaljenje pluća uzrokovano virusom, neoznačeno",
                        "Akutno zapaljenje slepog creva",
                        "Ciroza jetre uzrokovana alkoholom",
                        "Toksička bolest jetre zbog zastoja žuči",
                        "Kamen u bubregu"};
        String[] latin_desc = {
                "Tuberculosis pulmonum, methodis non specificatis confirmata",
                "Anaemia sideropenica",
                "Hypertensio arterialis essentialis (primaria)",
                "Stenosis valvulae aortae non rheumatica",
                "Influenza, virus non identificatum",
                "Pneumonia viralis, non specificata",
                "Appendicitis acuta",
                "Cirrhosis hepatis alcoholica",
                "Morbus hepatis toxicus cholestaticus",
                "Calculus renis"};

        for (int i = 0; i < mkb10.length; i += 1) {
            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setCode(mkb10[i]);
            diagnosis.setDescription(description[i]);
            diagnosis.setLatinDescription(latin_desc[i]);

            diagnosisRepository.save(diagnosis);
        }

        addDataFromFiles();
        makeSchedExam(patient);
    }

    private void makeSchedExam(Patient patient) {
        ScheduledMedExamination scheduledMedExamination = new ScheduledMedExamination();
        scheduledMedExamination.setPatient(patient);
        //                                                          5a2e71bb-e4ee-43dd-a3ad-28e043f8b435
        scheduledMedExamination.setLbzDoctor(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        scheduledMedExamination.setAppointmentDate(new Date());
        scheduledMedExamination.setNote("Pacijent ima bol u zuci");
        scheduledMedExamination.setLbzNurse(UUID.fromString("5a2e71bb-e4ee-43dd-55a3-28e043f8b435"));

        ScheduledMedExamination scheduledMedExamination1 = new ScheduledMedExamination();
        scheduledMedExamination1.setPatient(patient);
        scheduledMedExamination1.setLbzDoctor(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        scheduledMedExamination1.setAppointmentDate(calendar.getTime());
        scheduledMedExamination1.setNote("Pacijent oseca mucninu, ima glavobolju");
        scheduledMedExamination1.setLbzNurse(UUID.fromString("5a2e71bb-e4ee-43dd-55a3-28e043f8b435"));
        scheduledMedExamination1.setExaminationStatus(ExaminationStatus.U_TOKU);
        scheduledMedExamRepository.save(scheduledMedExamination1);

        ScheduledMedExamination scheduledMedExamination2 = new ScheduledMedExamination();
        scheduledMedExamination2.setPatient(patient);
        scheduledMedExamination2.setLbzDoctor(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(new Date());
        calendar1.add(Calendar.HOUR_OF_DAY, 2);
        scheduledMedExamination2.setAppointmentDate(calendar1.getTime());
        scheduledMedExamination2.setNote("Pacijent ima psihickih problema");
        scheduledMedExamination2.setLbzNurse(UUID.fromString("5a2e71bb-e4ee-43dd-55a3-28e043f8b435"));
        scheduledMedExamination2.setExaminationStatus(ExaminationStatus.OTKAZANO);
        scheduledMedExamRepository.save(scheduledMedExamination2);

        scheduledMedExamRepository.save(scheduledMedExamination);
    }

    private void addDataFromFiles() throws IOException, ParseException {
        Resource resource = new ClassPathResource("bootstrap-data/patients.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        List<String> lines = reader.lines().collect(Collectors.toList());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        List<Patient> patients = new ArrayList<>();
        List<HealthRecord> healthRecords = new ArrayList<>();

        for (String s : lines) {
            if (s.isEmpty())
                continue;
            String[] split = s.split(",");
            Patient patient = new Patient();
            patient.setJmbg(split[0]);
            patient.setFirstName(split[1]);
            patient.setLastName(split[2]);
            patient.setParentName(split[3]);
            patient.setGender(Gender.valueOfNotation(split[4]));
            patient.setBirthDate(sdf.parse(split[5]));
            patient.setBirthplace(split[6]);
            patient.setCitizenshipCountry(CountryCode.valueOf(split[7]));
            patient.setCountryOfLiving(CountryCode.valueOf(split[8]));
            patient.setLbp(UUID.fromString(split[9]));

            patient.setAddress(split[10]);
            patient.setPlaceOfLiving(split[11]);
            patient.setPhoneNumber(split[12]);
            patient.setEmail(split[13]);
            patient.setCustodianJmbg(split[14]);
            patient.setCustodianName(split[15]);
            patient.setFamilyStatus(FamilyStatus.valueOfNotation(split[16]));
            patient.setMaritalStatus(MaritalStatus.valueOfNotation(split[17]));
            patient.setChildrenNum(Integer.parseInt(split[18]));
            patient.setEducation(Education.valueOfNotation(split[19]));
            patient.setProfession(split[20]);


            HealthRecord healthRecord = new HealthRecord();
            healthRecord.setRegistrationDate(sdf.parse(split[21]));
            healthRecord.setBloodType(BloodType.valueOf(split[22]));
            healthRecord.setRhFactor(RHFactor.valueOfNotation(split[23]));

            patient.setHealthRecord(healthRecord);
            healthRecord.setPatient(patient);
            patients.add(patient);
            healthRecords.add(healthRecord);
        }
        reader.close();

        List<Patient> patientList = patientRepository.saveAll(patients);
        List<HealthRecord> healthRecordList = healthRecordRepository.saveAll(healthRecords);
        addMedExaminations(patients);
        addSchedExams(patientList);
        addAlergiesAndVacines(healthRecordList);
        makeVaccCovidAndSchedVacc(patientList);
    }

    private void makeVaccCovidAndSchedVacc(List<Patient> patientList) throws IOException {
        Resource resource = new ClassPathResource("bootstrap-data/vaccinationCovid.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        String line = reader.readLine();
        String[] split = line.split(",");
        int iter = 0;
        for (Patient patient : patientList){
            ScheduledVaccinationCovid scheduledVaccinationCovid = new ScheduledVaccinationCovid();
            scheduledVaccinationCovid.setPatient(patient);
            int minusMinutes = Integer.parseInt(split[0]) * iter;
            LocalDateTime schedVaccDate = LocalDateTime.now().plusDays(1).plusMinutes(minusMinutes);
            scheduledVaccinationCovid.setDateAndTime(schedVaccDate);
            scheduledVaccinationCovid.setNote(split[1]);
            scheduledVaccinationCovid.setSchedulerLbz(UUID.fromString(split[2]));

            AvailableTerm availableTerm = new AvailableTerm();
            availableTerm.setDateAndTime(schedVaccDate);
            availableTerm.setPbo(UUID.fromString(split[3]));
            availableTerm.setAvailableNursesNum(Integer.parseInt(split[4]));
            availableTerm.setScheduledTermsNum(Integer.parseInt(split[5]));
            availableTerm.setScheduledVaccinationCovids(new ArrayList<>());
            availableTerm.setScheduledTestings(new ArrayList<>());

            scheduledVaccinationCovid.setAvailableTerm(availableTerm);
            availableTerm.addScheduledVaccination(scheduledVaccinationCovid);

            availableTermRepository.save(availableTerm);
            scheduledVaccinationCovidRepository.save(scheduledVaccinationCovid);

            iter++;
        }
    }

    private void addMedExaminations(List<Patient> patients) throws IOException, ParseException {
        Resource resource = new ClassPathResource("bootstrap-data/examinations.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        List<String> lines = reader.lines().collect(Collectors.toList());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<Diagnosis> diagnoses = new ArrayList<>();
        List<MedicalExamination> examinations = new ArrayList<>();

        for (String s : lines) {
            if (s.isEmpty())
                continue;

            String[] split = s.split(",");
            MedicalExamination examination = new MedicalExamination();
            examination.setLbz(UUID.fromString(split[1]));
            examination.setDate(sdf.parse(split[2]));
            examination.setObjectiveFinding(split[3].replace(";", ","));
            examination.setMainSymptoms(split[4].replace(";", ","));
            examination.setCurrentIllness(split[5].replace(";", ","));
            examination.setAnamnesis(split[6].replace(";", ","));
            examination.setFamilyAnamnesis(split[7].replace(";", ","));
            examination.setPatientOpinion(split[8]);
            examination.setSuggestedTherapy(split[9].replace(";", ","));
            examination.setAdvice(split[10].replace(";", ","));

            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setCode(split[11]);
            diagnosis.setDescription(split[12]);
            diagnosis.setLatinDescription(split[13]);

            examination.setDiagnosis(diagnosis);
            Patient patient = patients.stream().filter(p -> p.getLbp().equals(UUID.fromString(split[0])))
                    .findFirst().orElse(null);
            assert patient != null;
            examination.setHealthRecord(patient.getHealthRecord());

            diagnoses.add(diagnosis);
            examinations.add(examination);
        }
        reader.close();

        diagnosisRepository.saveAll(diagnoses);
        medicalExaminationRepository.saveAll(examinations);

        addMedHistory(patients, diagnoses);

    }

    private void addMedHistory(List<Patient> patients, List<Diagnosis> diagnoses) throws IOException, ParseException {
        Resource resource = new ClassPathResource("bootstrap-data/histories.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        List<String> lines = reader.lines().collect(Collectors.toList());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<MedicalHistory> medHistories = new ArrayList<>();
        for (String s : lines) {
            if (s.isEmpty())
                continue;

            String[] split = s.split(",");

            MedicalHistory medicalHistory = new MedicalHistory();
            medicalHistory.setIllnessStart(sdf.parse(split[2]));
            medicalHistory.setTreatmentResult(TreatmentResult.valueOfNotation(split[3]));
            medicalHistory.setCurrentStateDescription(split[4].replace(";", ","));
            medicalHistory.setValid(Boolean.valueOf(split[5]));
            medicalHistory.setValidFrom(sdf.parse(split[6]));
            medicalHistory.setValidUntil(sdf.parse(split[7]));
            medicalHistory.setConfidential(Boolean.valueOf(split[8]));

            if (!split[9].equals("null"))
                medicalHistory.setIllnessEnd(sdf.parse(split[9]));

            Diagnosis diagnosis = diagnoses.stream().filter(d -> d.getCode().equals(split[1]))
                    .findFirst().orElse(null);

            medicalHistory.setDiagnosis(diagnosis);
            Patient patient = patients.stream().filter(p -> p.getLbp().equals(UUID.fromString(split[0])))
                    .findFirst().orElse(null);
            assert patient != null;
            medicalHistory.setHealthRecord(patient.getHealthRecord());
            medHistories.add(medicalHistory);
        }
        reader.close();

        medicalHistoryRepository.saveAll(medHistories);
    }

    private void addSchedExams(List<Patient> patients) throws IOException, ParseException {
        Resource resource = new ClassPathResource("bootstrap-data/schedexam.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        List<String> lines = reader.lines().collect(Collectors.toList());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<ScheduledMedExamination> scheduledMedExaminations = new ArrayList<>();

        for (String s : lines) {
            if (s.isEmpty())
                continue;
            String[] split = s.split(";");
            Patient patient = patients.stream().filter(p -> p.getLbp().equals(UUID.fromString(split[0])))
                    .findFirst()
                    .orElse(null);

            ScheduledMedExamination scheduledMedExamination = new ScheduledMedExamination();
            scheduledMedExamination.setPatient(patient);
            scheduledMedExamination.setLbzDoctor(UUID.fromString(split[1]));
            scheduledMedExamination.setAppointmentDate(sdf.parse(split[2]));
            scheduledMedExamination.setNote(split[3]);
            scheduledMedExamination.setLbzNurse(UUID.fromString(split[4]));
            scheduledMedExamination.setExaminationStatus(ExaminationStatus.valueOfNotation(split[5]));
            scheduledMedExaminations.add(scheduledMedExamination);
        }
        reader.close();

        scheduledMedExamRepository.saveAll(scheduledMedExaminations);
    }

    private void addAlergiesAndVacines(List<HealthRecord> healthRecords) throws IOException, ParseException {
        List<Allergen> allergens = allergenRepository.findAll();
        List<Vaccine> vaccines = vaccineRepository.findAll();
        List<Vaccination> vaccinations = new ArrayList<>();
        List<Allergy> allergies = new ArrayList<>();

        Resource resource = new ClassPathResource("bootstrap-data/allergyVaccines.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        List<String> lines = reader.lines().collect(Collectors.toList());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (String s : lines) {
            if (s.isEmpty())
                continue;
            String[] split = s.split(";");
            HealthRecord healthRecord = healthRecords.stream()
                    .filter(h -> h.getPatient().getLbp().equals(UUID.fromString(split[0])))
                    .findFirst()
                    .orElse(null);
            List<String> vaccinesToAdd = Arrays.asList(split[1].split(",").clone());
            List<String> dates = Arrays.asList(split[2].split(",").clone());
            List<Vaccine> vaccineList = vaccines.stream()
                    .filter(f -> vaccinesToAdd.contains(f.getName()))
                    .collect(Collectors.toList());
            for (int i = 0; i < vaccineList.size(); i++) {
                Vaccination vaccination = new Vaccination();
                vaccination.setVaccinationDate(sdf.parse(dates.get(i)));
                vaccination.setVaccine(vaccineList.get(i));
                vaccination.setHealthRecord(healthRecord);
                vaccinations.add(vaccination);
            }

            List<String> allergensToAdd = Arrays.asList(split[3].split(",").clone());
            List<Allergen> allergenList = allergens.stream()
                    .filter(a -> allergensToAdd.contains(a.getName()))
                    .collect(Collectors.toList());
            for (Allergen a : allergenList) {
                Allergy allergy = new Allergy();
                allergy.setAllergen(a);
                allergy.setHealthRecord(healthRecord);
                allergies.add(allergy);
            }
        }

        reader.close();

        vaccinationRepository.saveAll(vaccinations);
        allergyRepository.saveAll(allergies);
    }

    private HospitalRoom makeRoom() {
        HospitalRoom hospitalRoom = new HospitalRoom();

        hospitalRoom.setPbo(UUID.fromString("c0979e25-2bb1-4582-87a9-aa175777a65d"));
        hospitalRoom.setDescription("Bolnička soba");
        hospitalRoom.setRoomName("Soba 1");
        hospitalRoom.setRoomNumber(1);
        hospitalRoom.setCapacity(15);
        hospitalRoom.setOccupation(1);
        hospitalRoom.setRespirators(5);

        hospitalRoomRepository.save(hospitalRoom);
        return hospitalRoom;
    }

    private void makeHospitalization(HospitalRoom hospitalRoom) {
        Hospitalization hospitalization = new Hospitalization();

        hospitalization.setRegisterLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        hospitalization.setDoctorLBZ(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        hospitalization.setHospitalRoom(hospitalRoom);
        hospitalization.setPatient(patientRepository.findByLbp(UUID.fromString("c1c8ba08-966a-4cc5-b633-d1ef15d7caaf")).get());
        hospitalization.setDiagnosis("Lom ruke");
        hospitalization.setReceiptDate(new Date());

        hospitalizationRepository.save(hospitalization);
    }

    private void makeAppointment() {
        Appointment appointment = new Appointment();

        appointment.setStatus(AppointmentStatus.ZAKAZAN);
        appointment.setPbo(UUID.fromString("c0979e25-2bb1-4582-87a9-aa175777a65d"));
        appointment.setReceiptDate(new Date());
        appointment.setEmployeeLBZ(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        appointment.setPatient(patientRepository.findByLbp(UUID.fromString("c1c8ba08-966a-4cc5-b633-d1ef15d7caaf")).get());

        appointmentRepository.save(appointment);
    }
}