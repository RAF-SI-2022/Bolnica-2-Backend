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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    public void run(String... args) {
        Hospital hospital = new Hospital();
        hospital.setAddress("Dimitrija Tucovića 161");
        hospital.setDateOfEstablishment(new Date());
        hospital.setActivity("Hirurgija");
        hospital.setPlace("Beograd");
        hospital.setFullName("KBC Zvezdara - Klinika za hirurgiju \"Nikola Spasic\"");
        hospital.setPbb(UUID.randomUUID());
        hospital.setShortName("KBC Zvezdara");

        hospital = hospitalRepository.save(hospital);

        Department chirurgyDepartment = new Department();
        chirurgyDepartment.setName("Hirurgija");
        chirurgyDepartment.setHospital(hospital);
        chirurgyDepartment.setPbo(UUID.randomUUID());

        Department laboratoryDepartment = new Department();
        laboratoryDepartment.setName("Laboratorija");
        laboratoryDepartment.setHospital(hospital);
        laboratoryDepartment.setPbo(UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));

        Department diagnosticDepartment = new Department();
        diagnosticDepartment.setName("Dijagnostika");
        diagnosticDepartment.setHospital(hospital);
        diagnosticDepartment.setPbo(UUID.randomUUID());

        chirurgyDepartment = departmentRepository.save(chirurgyDepartment);
        departmentRepository.save(laboratoryDepartment);
        departmentRepository.save(diagnosticDepartment);

        Permission adminPermission = new Permission();
        adminPermission.setName("ROLE_ADMIN");

        Permission drSpecOdeljenjaPermission = new Permission();
        drSpecOdeljenjaPermission.setName("ROLE_DR_SPEC_ODELJENJA");

        Permission drSpecPermission = new Permission();
        drSpecPermission.setName("ROLE_DR_SPEC");

        Permission drSpecPovPermission = new Permission();
        drSpecPovPermission.setName("ROLE_DR_SPEC_POV");

        Permission visaMedSestraPermission = new Permission();
        visaMedSestraPermission.setName("ROLE_VISA_MED_SESTRA");

        Permission medSestraPermission = new Permission();
        medSestraPermission.setName("ROLE_MED_SESTRA");

        Permission visiLabTehnicar = new Permission();
        visiLabTehnicar.setName("ROLE_VISI_LAB_TEHNICAR");

        Permission labTehnicar = new Permission();
        labTehnicar.setName("ROLE_LAB_TEHNICAR");

        Permission medBiohemicar = new Permission();
        medBiohemicar.setName("ROLE_MED_BIOHEMICAR");

        Permission specMedBiohemije = new Permission();
        specMedBiohemije.setName("ROLE_SPEC_MED_BIOHEMIJE");

        Permission receptionistPermission = new Permission();
        receptionistPermission.setName("ROLE_RECEPCIONER");

        List<Permission> adminPermissions = new ArrayList<>();
        List<Permission> medSestraPermissions = new ArrayList<>();
        visaMedSestraPermission = permissionsRepository.save(visaMedSestraPermission);
        medSestraPermission = permissionsRepository.save(medSestraPermission);


        adminPermissions.add(permissionsRepository.save(adminPermission));
        adminPermissions.add(permissionsRepository.save(drSpecOdeljenjaPermission));
        adminPermissions.add(permissionsRepository.save(drSpecPermission));
        adminPermissions.add(permissionsRepository.save(drSpecPovPermission));
        adminPermissions.add(permissionsRepository.save(visiLabTehnicar));
        adminPermissions.add(permissionsRepository.save(labTehnicar));
        adminPermissions.add(permissionsRepository.save(medBiohemicar));
        adminPermissions.add(permissionsRepository.save(specMedBiohemije));
        adminPermissions.add(permissionsRepository.save(receptionistPermission));
        adminPermissions.add(visaMedSestraPermission);
        adminPermissions.add(medSestraPermission);

        medSestraPermissions.add(visaMedSestraPermission);
        medSestraPermissions.add(medSestraPermission);

        User user = new User();
        user.setEmail("balkan.medic2023@outlook.com");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setUsername("admin");
        user.setDepartment(chirurgyDepartment);
        user.setResidentialAddress("Admin address");
        user.setPermissions(adminPermissions);
        user.setJMBG("23112412212");
        user.setPhone("02132123132");
        user.setPlaceOfLiving("Place of living");
        user.setDateOfBirth(new Date());
        user.setGender("Muski");
        user.setFirstName("Admin");
        user.setLastName("Adminovic");
        user.setTitle(Title.DR_SCI_MED);
        user.setProfession(Profession.SPEC_HIRURG);
        user.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        User medSestra = new User();
        medSestra.setEmail("medsestra@something.com");
        medSestra.setPassword(passwordEncoder.encode("medsestra"));
        medSestra.setUsername("medsestra");
        medSestra.setDepartment(laboratoryDepartment);
        medSestra.setResidentialAddress("address");
        medSestra.setPermissions(medSestraPermissions);
        medSestra.setJMBG("463453453434");
        medSestra.setPhone("0213123112");
        medSestra.setPlaceOfLiving("Place");
        medSestra.setDateOfBirth(new Date());
        medSestra.setGender("Zenski");
        medSestra.setFirstName("Medicinska");
        medSestra.setLastName("Sestra");
        medSestra.setTitle(Title.DIPL_FARM);
        medSestra.setProfession(Profession.MED_SESTRA);
        medSestra.setLbz(UUID.fromString("3e1a51ab-a3aa-1add-a3ad-28e043f8b435"));

        userRepository.save(medSestra);
        userRepository.save(user);
    }
}