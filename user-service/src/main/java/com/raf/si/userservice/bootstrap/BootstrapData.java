package com.raf.si.userservice.bootstrap;

import com.raf.si.userservice.model.*;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.ShiftType;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class  BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PermissionsRepository permissionsRepository;
    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;
    private final ShiftTimeRepository shiftTimeRepository;
    private final ShiftRepository shiftRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapData(UserRepository userRepository, PermissionsRepository permissionsRepository,
                         DepartmentRepository departmentRepository, HospitalRepository hospitalRepository,
                         ShiftTimeRepository shiftTimeRepository, ShiftRepository shiftRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.permissionsRepository = permissionsRepository;
        this.departmentRepository = departmentRepository;
        this.hospitalRepository = hospitalRepository;
        this.shiftTimeRepository = shiftTimeRepository;
        this.shiftRepository = shiftRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws ParseException, IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Hospital kbcZvezdara = addHospital("Dimitrija Tucovića 161", "01/12/1935", "Zdravstvena delatnost",
                "Beograd", "Kliničko bolnički centar \"Zvezdara\"", "KBC Zvezdara");


        Hospital ukcSrbija = addHospital("Pasterova 2", "01/01/1983", "Zdravstvena delatnost",
                "Beograd", "Univerzitetski klinički centar Srbije", "UKC Srbije");


        Hospital kbcBezanijskaKosa = addHospital("Bezanijska kosa bb", "01/01/2000", "Zdravstvena delatnost",
                "Beograd", "Kliničko bolnički centar \"Bežanijska kosa\"", "KBC Bežanijska kosa");


        Hospital kbcZemun = addHospital("Vukova 9", "25/02/1784", "Zdravstvena delatnost",
                "Beograd", "Kliničko bolnički centar \"Zemun\"", "KBC Zemun");


        addDepartmentsToHospital(
                List.of("Ginekologija", "Hirurgija", "Interne bolesti",
                        "Pedijatrija", "Psihijatrija", "Očne bolesti",
                        "Laboratorija", "Dijagnostika", "Stacionar"),
                List.of("0475d06f-c43d-4cf2-828e-f1545e15f768", "b259d83b-e6fb-4b8b-8104-d96a6ad1ad5a",
                        "be7fed71-9a96-4644-8d0e-f80a216f77d6", "8436ec8b-19de-4d18-bd5c-469bd3512396",
                        "620763b5-a3f7-449f-86d9-fcff72aeaf32", "f8894d56-1263-4a9f-85a5-9e17dc9351ce",
                        "92c471d0-3980-41fb-a2bd-6a3e0e63e5e2", "29dde67e-d3a6-4983-babe-0a7b33c62608",
                        "c0979e25-2bb1-4582-87a9-aa175777a65d"),
                kbcZvezdara
        );

        addDepartmentsToHospital(
                List.of("Infektivne i tropske bolesti", "Interne bolesti",
                        "Psihijatrija", "Neurologija", "Hirurgija",
                        "Ginekologija i akušerstvo", "Očne bolesti",
                        "Dermatovenerologija", "Onkologija",
                        "Laboratorija", "Dijagnostika", "Stacionar"),
                List.of("a8b47778-52c2-4b02-9554-53e0196840ea", "50e3684b-e7d2-47c5-aa5d-0402f57ebe8d",
                        "72d0a198-5267-42f6-afc9-59d906bfe9c0","d8764da4-a048-4ed5-9efe-ae7ebb700a47",
                        "f3a3635d-f0d2-44c2-af25-06f18c72740c", "6d5ca2f5-6f71-462d-80c9-131ce39e8124",
                        "4eefde68-24f3-4492-b1ed-e0a70bc2e14e", "ec047d3c-fbac-4276-a526-7d353964bf96",
                        "fc39d46d-6662-42e8-80ca-9226eed4534d", "8c9169e8-01ff-4172-b537-9e816f102750",
                        "3f12eda8-5af1-45a1-9a2f-eff348fcf8b9", "a4e94a9a-d4cb-4323-831a-da823bf8ff7a"
                        ),
                ukcSrbija
        );

        addDepartmentsToHospital(
                List.of("Interne bolesti", "Hirurgija",
                        "Onkologija", "Laboratorija", "Dijagnostika", "Stacionar"),
                List.of("baeeee67-231a-40e3-9f8f-7b0a609e25f1", "1647332e-2740-4839-adb7-762d8be6ac32",
                        "ed2245ca-a66b-4402-a654-ca2ec5926480", "ccbba084-b9f3-4fe5-bf90-714795b735ad",
                        "46f9e670-401d-4147-aab7-719d0047a941", "3e6eba4c-40a6-45d5-b20e-f4338c1c74d2"),
                kbcBezanijskaKosa
        );

        addDepartmentsToHospital(
                List.of("Interne bolesti", "Neurologija",
                        "Hirurgija", "Ginekologija i akušerstvo",
                        "Onkologija","Laboratorija", "Dijagnostika", "Stacionar", "Covid odsek"),
                List.of("4349eb95-d671-41c6-8cb1-389a45466cde", "c2de9275-ee7d-4994-85e7-ab433c843529",
                        "4260b200-9abf-42c5-acdf-8050fd55783e", "bf027665-8d73-4ec1-8f05-9e73ca4434a0",
                        "73e69114-7e40-4bd9-a69d-1599ba011baf", "13531c13-ac0b-465c-9b2c-56ecd5bf3474",
                        "4812d5d8-f43c-432a-a12c-7ff88c28fd4d", "49f62e58-d996-4f7d-bded-965a8719454f", "50869452-02f6-4ef7-8592-24d342cd70d1"),
                kbcZemun
        );

        Permission adminPermission = addPermission("ROLE_ADMIN", 30);

        Permission drSpecOdeljenjaPermission = addPermission("ROLE_DR_SPEC_ODELJENJA", 29);

        Permission drSpecPermission = addPermission("ROLE_DR_SPEC", 28);

        Permission drSpecPovPermission = addPermission("ROLE_DR_SPEC_POV", 28);

        Permission visaMedSestraPermission = addPermission("ROLE_VISA_MED_SESTRA", 27);

        Permission medSestraPermission = addPermission("ROLE_MED_SESTRA", 26);

        Permission visiLabTehnicar = addPermission("ROLE_VISI_LAB_TEHNICAR", 27);

        Permission labTehnicar = addPermission("ROLE_LAB_TEHNICAR", 26);

        Permission medBiohemicar = addPermission("ROLE_MED_BIOHEMICAR", 26);

        Permission specMedBiohemije = addPermission("ROLE_SPEC_MED_BIOHEMIJE", 26);

        Permission receptionistPermission = addPermission("ROLE_RECEPCIONER", 24);

        List<Permission> adminPermissions = new ArrayList<>(
                List.of(adminPermission, drSpecOdeljenjaPermission, drSpecPermission, drSpecPovPermission, visiLabTehnicar,
                        labTehnicar, medBiohemicar, specMedBiohemije, receptionistPermission, visaMedSestraPermission,
                        medSestraPermission)
        );

        List<Permission> medSestraPermissions = new ArrayList<>(List.of(medSestraPermission, visaMedSestraPermission));

        User user = new User();
        user.setEmail("balkan.medic2023@outlook.com");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setUsername("admin");
        user.setDepartment(departmentRepository.
                findDepartmentByNameAndHospital_ShortName("Hirurgija", ukcSrbija.getShortName()));
        user.setResidentialAddress("Ljubiše Jelenkovića 73");
        user.setPermissions(adminPermissions);
        user.setJMBG("2013012312143");
        user.setPhone("02132123132");
        user.setPlaceOfLiving("Beograd, Srbija");
        user.setDateOfBirth(sdf.parse("02/05/1965"));
        user.setGender("Muski");
        user.setFirstName("Zlatibor");
        user.setLastName("Lončar");
        user.setTitle(Title.PROF_DR_MED);
        user.setProfession(Profession.SPEC_HIRURG);
        user.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        user = findUserDaysOff(user);

        User medSestra = new User();
        medSestra.setEmail("edotlic10320rn@raf.rs");
        medSestra.setPassword(passwordEncoder.encode("medsestra"));
        medSestra.setUsername("medsestra");
        medSestra.setDepartment(departmentRepository.
                findDepartmentByNameAndHospital_ShortName("Laboratorija", ukcSrbija.getShortName()));
        medSestra.setResidentialAddress("address");
        medSestra.setPermissions(medSestraPermissions);
        medSestra.setJMBG("2002010359910");
        medSestra.setPhone("0621231231");
        medSestra.setPlaceOfLiving("Cara Dušana 141");
        medSestra.setDateOfBirth(sdf.parse("18/02/1995"));
        medSestra.setGender("Zenski");
        medSestra.setFirstName("Emilija");
        medSestra.setLastName("Dotlić");
        medSestra.setTitle(Title.DIPL_FARM);
        medSestra.setProfession(Profession.MED_SESTRA);
        medSestra.setLbz(UUID.fromString("3e1a51ab-a3aa-1add-a3ad-28e043f8b435"));
        medSestra = findUserDaysOff(medSestra);

        userRepository.save(user);
        userRepository.save(medSestra);

        addShiftTimes();

        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 1, 1);
        LocalDateTime endTime = startTime.plusHours(11);

        Shift shift = new Shift();
        shift.setShiftType(ShiftType.MEDJUSMENA);
        shift.setUser(user);
        shift.setStartTime(startTime);
        shift.setEndTime(endTime);

        Shift nurseShift = new Shift();
        nurseShift.setShiftType(ShiftType.MEDJUSMENA);
        nurseShift.setUser(medSestra);
        nurseShift.setStartTime(startTime);
        nurseShift.setEndTime(endTime);

        shiftRepository.save(shift);
        shiftRepository.save(nurseShift);

        addOtherUsers();
    }

    private Hospital addHospital(String address, String date, String activity,
                                 String place, String fullName, String shortName) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Hospital hospital = new Hospital();

        hospital.setAddress(address);
        hospital.setDateOfEstablishment(sdf.parse(date));
        hospital.setActivity(activity);
        hospital.setPlace(place);
        hospital.setFullName(fullName);
        hospital.setShortName(shortName);
        hospital.setPbb(UUID.randomUUID());

        return hospitalRepository.save(hospital);
    }

    private void addDepartmentsToHospital(List<String> departmentNames, List<String> uuids, Hospital hospital) {
        for (int i = 0; i < departmentNames.size(); i++) {
            Department department = new Department();
            department.setName(departmentNames.get(i));
            department.setHospital(hospital);
            department.setPbo(UUID.fromString(uuids.get(i)));
            departmentRepository.save(department);
        }
    }

    private Permission addPermission(String role, int daysOff) {
        Permission permission = new Permission();
        permission.setName(role);
        permission.setDaysOff(daysOff);
        permission = permissionsRepository.save(permission);

        return permission;
    }

    private void addShiftTimes() {
        addShiftTime(ShiftType.PRVA_SMENA, LocalTime.of(6, 0), LocalTime.of(14, 0));
        addShiftTime(ShiftType.DRUGA_SMENA, LocalTime.of(14, 0), LocalTime.of(22, 0));
        addShiftTime(ShiftType.TRECA_SMENA, LocalTime.of(22, 0), LocalTime.of(6, 0));
        addShiftTime(ShiftType.MEDJUSMENA, null, null);
        addShiftTime(ShiftType.SLOBODAN_DAN, null, null);
    }

    private void addShiftTime(ShiftType shiftType, LocalTime startTime, LocalTime endTime) {
        ShiftTime shiftTime = new ShiftTime();

        shiftTime.setShiftType(shiftType);
        shiftTime.setStartTime(startTime);
        shiftTime.setEndTime(endTime);

        shiftTimeRepository.save(shiftTime);
    }

    private void addOtherUsers() throws IOException, ParseException {
        Resource resource = new ClassPathResource("bootstrap-data/employees.txt");

        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

        List<String> lines = reader.lines().collect(Collectors.toList());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for(String s: lines) {
            if(s.isEmpty())
                continue;
            String[] split = s.split(",");
            String usernamePassword = split[0].substring(0, split[0].indexOf('@'));
            User user = new User();
            user.setEmail(split[0]);
            user.setPassword(passwordEncoder.encode(usernamePassword));
            user.setUsername(usernamePassword);
            user.setDepartment(departmentRepository.
                    findDepartmentByNameAndHospital_ShortName(split[1], split[2]));
            user.setResidentialAddress(split[3]);
            user.setPermissions(permissionsRepository.findPermissionsByNameIsIn(Arrays.asList(split[4].split(";"))));
            user.setJMBG(split[5]);
            user.setPhone(split[6]);
            user.setPlaceOfLiving(split[7].replace(";", ","));
            user.setDateOfBirth(sdf.parse(split[8]));
            user.setGender(split[9]);
            user.setFirstName(split[10]);
            user.setLastName(split[11]);
            user.setTitle(Title.valueOfNotation(split[12]));
            user.setProfession(Profession.valueOfNotation(split[13]));
            user.setLbz(UUID.fromString(split[14]));
            user.setCovidAccess(true);
            user = findUserDaysOff(user);
            userRepository.save(user);

            addShiftsForUser(user);
        }

    }

    private void addShiftsForUser(User user) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        for (int i = 0; i < 10; i++) {
            Shift shift = new Shift();

            shift.setUser(user);
            shift.setShiftType(ShiftType.PRVA_SMENA);

            LocalDateTime startTime = now.plusDays(i).plusHours(6);
            LocalDateTime endTime = startTime.plusHours(8);

            shift.setStartTime(startTime);
            shift.setEndTime(endTime);

            shiftRepository.save(shift);
        }
    }

    private User findUserDaysOff(User user) {
        Permission maxDaysOffPerm = user.getPermissions()
                .stream()
                .max(Comparator.comparing(Permission::getDaysOff))
                .orElseThrow(NoSuchElementException::new);

        user.setDaysOff(maxDaysOffPerm.getDaysOff());
        return user;
    }
}