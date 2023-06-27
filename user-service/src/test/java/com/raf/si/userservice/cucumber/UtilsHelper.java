package com.raf.si.userservice.cucumber;

import com.raf.si.userservice.dto.request.AddShiftRequest;
import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

public class UtilsHelper {

    private final JwtUtil jwtUtil;

    public UtilsHelper(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String generateToken(User user) {
        Claims claims = Jwts.claims();
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("title", user.getTitle().getNotation());
        claims.put("profession", user.getProfession().getNotation());
        claims.put("pbo", user.getDepartment().getPbo());
        claims.put("departmentName", user.getDepartment().getName());
        claims.put("pbb", user.getDepartment().getHospital().getPbb());
        claims.put("hospitalName", user.getDepartment().getHospital().getFullName());
        String[] roles = new String[]{"ROLE_ADMIN", "ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC",
                "ROLE_DR_SPEC_POV", "ROLE_VISA_MED_SESTRA", "ROLE_MED_SESTRA"};
        claims.put("permissions", roles);
        claims.put("covidAccess", user.isCovidAccess());
        return jwtUtil.generateToken(claims, user.getLbz().toString());
    }

    public String generateToken(User user, String[] roles) {
        Claims claims = Jwts.claims();
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("title", user.getTitle().getNotation());
        claims.put("profession", user.getProfession().getNotation());
        claims.put("pbo", user.getDepartment().getPbo());
        claims.put("departmentName", user.getDepartment().getName());
        claims.put("pbb", user.getDepartment().getHospital().getPbb());
        claims.put("hospitalName", user.getDepartment().getHospital().getFullName());
        claims.put("permissions", roles);
        claims.put("covidAccess", user.isCovidAccess());
        return jwtUtil.generateToken(claims, user.getLbz().toString());
    }

    public String generateEmployeeToken(User user) {
        Claims claims = Jwts.claims();
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("title", user.getTitle().getNotation());
        claims.put("profession", user.getProfession().getNotation());
        claims.put("pbo", user.getDepartment().getPbo());
        claims.put("departmentName", user.getDepartment().getName());
        claims.put("pbb", user.getDepartment().getHospital().getPbb());
        claims.put("hospitalName", user.getDepartment().getHospital().getFullName());
        String[] roles = new String[]{"ROLE_VISA_MED_SESTRA", "ROLE_MED_SESTRA"};
        claims.put("permissions", roles);
        claims.put("covidAccess", user.isCovidAccess());
        return jwtUtil.generateToken(claims, user.getLbz().toString());
    }

    public CreateUserRequest createUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setFirstName("Pera");
        createUserRequest.setLastName("Peric");
        createUserRequest.setDateOfBirth(new Date());
        createUserRequest.setGender("muski");
        createUserRequest.setJmbg("5312412312");
        createUserRequest.setResidentialAddress("Perina adresa");
        createUserRequest.setPlaceOfLiving("Srbija");
        createUserRequest.setPhone("0351341221");
        createUserRequest.setEmail("pera@gmail.com");
        createUserRequest.setTitle(Title.MR.getNotation());
        createUserRequest.setProfession(Profession.SPEC_HIRURG.getNotation());
        createUserRequest.setDepartmentId(1L);
        createUserRequest.setPermissions(new String[]{"ROLE_DR_SPEC"});

        return createUserRequest;
    }

    public UpdateUserRequest createUpdateUserRequest() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setDepartmentId(1L);
        updateUserRequest.setJmbg("5312412312");
        updateUserRequest.setEmail("pera123@gmail.com");
        updateUserRequest.setGender("muski");
        updateUserRequest.setFirstName("Petar");
        updateUserRequest.setLastName("Petrovic");
        updateUserRequest.setPhone("0123123112");
        updateUserRequest.setUsername("pera");
        updateUserRequest.setDateOfBirth(new Date());
        updateUserRequest.setProfession(Profession.SPEC_GASTROENTEROLOG.getNotation());
        updateUserRequest.setTitle(Title.MR.getNotation());
        updateUserRequest.setPlaceOfLiving("Srbija");
        updateUserRequest.setResidentialAddress("Adresa");

        return updateUserRequest;
    }

    public User makeUser() {
        User user = new User();

        user.setFirstName("Ime");
        user.setLastName("Prezime");
        user.setJMBG("1010101010");
        user.setDaysOff(10);
        user.setUsername("username");
        user.setLbz(UUID.randomUUID());
        user.setUsedDaysOff(0);
        user.setDateOfBirth(new Date());
        user.setPassword("psw");
        user.setEmail("mail@mail.com");
        user.setPasswordToken(UUID.randomUUID());
        user.setResidentialAddress("add");
        user.setProfession(Profession.LAB_TEHNICAR);
        user.setGender("Muski");
        user.setTitle(Title.MR);
        user.setPhone("00000000");
        user.setPlaceOfLiving("POL");

        return user;
    }
}
