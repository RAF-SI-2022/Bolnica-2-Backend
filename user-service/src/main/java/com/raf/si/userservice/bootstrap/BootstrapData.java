package com.raf.si.userservice.bootstrap;

import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Hospital;
import com.raf.si.userservice.model.Permission;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.repository.DepartmentRepository;
import com.raf.si.userservice.repository.HospitalRepository;
import com.raf.si.userservice.repository.PermissionsRepository;
import com.raf.si.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PermissionsRepository permissionsRepository;
    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapData(UserRepository userRepository, PermissionsRepository permissionsRepository,
                         DepartmentRepository departmentRepository, HospitalRepository hospitalRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.permissionsRepository = permissionsRepository;
        this.departmentRepository = departmentRepository;
        this.hospitalRepository = hospitalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws ParseException, IOException, URISyntaxException {
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
                List.of("Klinika za ginekologiju", "Klinika za hirurgiju", "Klinika za interne bolesti",
                        "Klinika za pedijatriju", "Klinika za psihijatriju", "Klinika za očne bolesti",
                        "Klinika za uho grlo i nos", "Služba za dijagnostičke procedure"),
                kbcZvezdara
        );

        addDepartmentsToHospital(
                List.of("Klinika za infektivne i tropske bolesti", "Klinika za interne bolesti",
                        "Klinika za psihijatriju", "Klinika za neurologiju", "Klinika za hirurgiju",
                        "Klinika za ginekologiju i akušerstvo", "Klinika za očne bolesti",
                        "Klinika za uho, grlo i nos", "Klinika za dermatovenerologiju", "Klinika za onkologiju",
                        "Služba za dijagnostičke procedure"),
                ukcSrbija
        );

        addDepartmentsToHospital(
                List.of("Klinika za interne bolesti", "Klinika za hirurgiju",
                        "Klinika za onkologiju", "Služba za dijagnostičke procedure"),
                kbcBezanijskaKosa
        );

        addDepartmentsToHospital(
                List.of("Klinika za interne bolesti", "Klinika za neurologiju",
                        "Klinika za hirurgiju", "Klinika za ginekologiju i akušerstvo",
                        "Klinika za onkologiju", "Služba za dijagnostičke procedure"),
                kbcZemun
        );

        Permission adminPermission = addPermission("ROLE_ADMIN");

        Permission drSpecOdeljenjaPermission = addPermission("ROLE_DR_SPEC_ODELJENJA");

        Permission drSpecPermission = addPermission("ROLE_DR_SPEC");

        Permission drSpecPovPermission = addPermission("ROLE_DR_SPEC_POV");

        Permission visaMedSestraPermission = addPermission("ROLE_VISA_MED_SESTRA");

        Permission medSestraPermission = addPermission("ROLE_MED_SESTRA");

        Permission visiLabTehnicar = addPermission("ROLE_VISI_LAB_TEHNICAR");

        Permission labTehnicar = addPermission("ROLE_LAB_TEHNICAR");

        Permission medBiohemicar = addPermission("ROLE_MED_BIOHEMICAR");

        Permission specMedBiohemije = addPermission("ROLE_SPEC_MED_BIOHEMIJE");

        Permission receptionistPermission = addPermission("ROLE_RECEPCIONER");

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
                findDepartmentByNameAndHospital_ShortName("Klinika za hirurgiju", ukcSrbija.getShortName()));
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

        User medSestra = new User();
        medSestra.setEmail("edotlic10320rn@raf.rs");
        medSestra.setPassword(passwordEncoder.encode("medsestra"));
        medSestra.setUsername("medsestra");
        medSestra.setDepartment(departmentRepository.
                findDepartmentByNameAndHospital_ShortName("Služba za dijagnostičke procedure", ukcSrbija.getShortName()));
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

        userRepository.save(user);
        userRepository.save(medSestra);

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

    private void addDepartmentsToHospital(List<String> departmentNames, Hospital hospital) {
        for (String s : departmentNames) {
            Department department = new Department();
            department.setName(s);
            department.setHospital(hospital);
            department.setPbo(UUID.randomUUID());
            departmentRepository.save(department);
        }
    }

    private Permission addPermission(String role) {
        Permission permission = new Permission();
        permission.setName(role);
        permission = permissionsRepository.save(permission);

        return permission;
    }

    private void addOtherUsers() throws URISyntaxException, IOException, ParseException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("bootstrap-data/employees.txt");
        if (resource == null) {
            throw new IllegalArgumentException("Cannot load bootstrap file!");
        }

        List<String> lines = Files.readAllLines(Path.of(resource.toURI()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for(String s: lines) {
            if(s.isEmpty())
                continue;
            String[] split = s.split(",");
            String usernamePassword = split[0].substring(0, split[0].indexOf('@'));
            User user = new User();
            user.setEmail(split[0]);
            user.setPassword(passwordEncoder.encode(usernamePassword));
            user.setUsername(passwordEncoder.encode(usernamePassword));
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
            user.setLbz(UUID.randomUUID());
            userRepository.save(user);
        }

    }
}