package com.raf.si.patientservice.integration;

import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.model.enums.user.Profession;
import com.raf.si.patientservice.model.enums.user.Title;
import com.raf.si.patientservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UtilsHelper {
    private final JwtUtil jwtUtil;
    private final  int DURATION_OF_EXAM;
    public UtilsHelper(JwtUtil jwtUtil, int DURATION_OF_EXAM) {
        this.jwtUtil=jwtUtil;
        this.DURATION_OF_EXAM=DURATION_OF_EXAM;
    }

    public String generateDocaToken(){
        String token = "3e1a51ab-a3aa-1add-a3ad-28e043f8b435";
        String[] roles= new String[]{"ROLE_DR_SPEC"};
        String profession= "Spec. pulmolog";
        return  generateToken(List.of(roles), profession, token);
    }

    public String generateNurseToken(){
        String token = "3e1a51ab-a3aa-1add-a3ad-28e043f8b435";
        String[] roles= new String[]{"ROLE_VISA_MED_SESTRA","ROLE_MED_SESTRA"};
        String profession= "Med. sestra";
        return  generateToken(List.of(roles), profession, token);
    }

    public String generateNurseTokenValid(){
        return generateToken();
    }

    private String generateToken(List<String> roles, String profession, String token){
        Claims claims= Jwts.claims();
        claims.put("firstName", "User");
        claims.put("lastName","LastOfUs");
        claims.put("title",profession);
        claims.put("pbo",UUID.fromString("124b3c7c-cf49-11ed-afa1-0242ac120002"));
        claims.put("departmentName","Dijagnostika");
        claims.put("pbb",UUID.randomUUID());
        claims.put("hospitalName", "NoviBeograd");
        claims.put("permissions",roles);
        return  jwtUtil.generateToken(claims, token);
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
        String[] roles = new String[]{"ROLE_ADMIN", "ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC",
                "ROLE_DR_SPEC_POV", "ROLE_VISA_MED_SESTRA", "ROLE_MED_SESTRA"};
        claims.put("permissions", roles);
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

    public SchedMedExamRequest createSchedMedExamRequest(int validDate){
        SchedMedExamRequest schedMedExamRequest= new SchedMedExamRequest();
        schedMedExamRequest.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        schedMedExamRequest.setLbzDoctor(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        schedMedExamRequest.setAppointmentDate(new Date(new Date().getTime()- (DURATION_OF_EXAM + 4*validDate ) * 60 * 1000));
        schedMedExamRequest.setLbzNurse(UUID.fromString("3e1a51ab-a3aa-1add-a3ad-28e043f8b435"));

        return schedMedExamRequest;
    }

    public UpdateSchedMedExamRequest createUpdateSchedMedExamRequest(String status) {
        UpdateSchedMedExamRequest updateSchedMedExamRequest= new UpdateSchedMedExamRequest();

        updateSchedMedExamRequest.setId(1L);
        updateSchedMedExamRequest.setNewStatus(status);

        return updateSchedMedExamRequest;
    }

    public ScheduledMedExamination createSchedMedExamination() {
        ScheduledMedExamination scheduledMedExamination= new ScheduledMedExamination();
        scheduledMedExamination.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        scheduledMedExamination.setLbzDoctor(UUID.fromString("266a1e0c-cf45-11ed-afa1-0242ac120002"));
        scheduledMedExamination.setAppointmentDate(new Date(new Date().getTime()- (DURATION_OF_EXAM + 5 ) * 60 * 1000));
        scheduledMedExamination.setLbzNurse(UUID.fromString("3e1a51ab-a3aa-1add-a3ad-28e043f8b435"));
        return scheduledMedExamination;
    }
}
