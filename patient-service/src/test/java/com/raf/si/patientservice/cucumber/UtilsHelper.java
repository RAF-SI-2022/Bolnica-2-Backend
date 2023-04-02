package com.raf.si.patientservice.cucumber;

import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.model.enums.user.Profession;
import com.raf.si.patientservice.model.enums.user.Title;
import com.raf.si.patientservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class UtilsHelper {

    private final JwtUtil jwtUtil;

    public UtilsHelper(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String generateToken() {
        Claims claims = Jwts.claims();
        claims.put("firstName", "admin");
        claims.put("lastName", "adminovic");
        claims.put("title", Title.DR_SCI_MED.getNotation());
        claims.put("profession", Profession.SPEC_HIRURG.getNotation());
        claims.put("pbo", UUID.randomUUID());
        claims.put("departmentName", "departman");
        claims.put("pbb", UUID.randomUUID());
        claims.put("hospitalName", "Bolnica");
        String[] roles = new String[]{"ROLE_MED_SESTRA"};
        claims.put("permissions", Arrays.asList(roles));
        return jwtUtil.generateToken(claims, "5a2e71bb-e4ee-43dd-a3ad-28e043f8b435");
    }

    public PatientRequest makePatientRequest(){
        PatientRequest patientRequest = new PatientRequest();

        patientRequest.setJmbg("1342002345612");
        patientRequest.setFirstName("Pacijent");
        patientRequest.setLastName("Pacijentovic");
        patientRequest.setParentName("Roditelj");
        patientRequest.setGender("Muški");
        patientRequest.setBirthDate(new Date());
        patientRequest.setDeathDate(new Date());
        patientRequest.setBirthplace("Resnjak");
        patientRequest.setCitizenshipCountry("SRB");
        patientRequest.setCountryOfLiving("AFG");

        patientRequest.setAddress("Jurija Gagarina 16");
        patientRequest.setPlaceOfLiving("Novi Beograd");
        patientRequest.setPhoneNumber("0601234567");
        patientRequest.setEmail("pacijent.pacijentovic@gmail.com");
        patientRequest.setCustodianJmbg("0101987123456");
        patientRequest.setCustodianName("Staratelj Starateljovic");
        patientRequest.setProfession("Programer");
        patientRequest.setChildrenNum(2);
        patientRequest.setEducation("Osnovno obrazovanje");
        patientRequest.setMaritalStatus("Razveden");
        patientRequest.setFamilyStatus("Usvojen");

        return patientRequest;
    }
}
