package com.raf.si.patientservice.integration;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

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

    public SchedMedExamRequest createSchedMedExamRequest(int validDate){
        SchedMedExamRequest schedMedExamRequest= new SchedMedExamRequest();
        schedMedExamRequest.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        schedMedExamRequest.setLbzDoctor(UUID.fromString("266a1e0c-cf45-11ed-afa1-0242ac120002"));
        schedMedExamRequest.setAppointmentDate(new Date(new Date().getTime()- (DURATION_OF_EXAM + 4*validDate ) * 60 * 1000));
        schedMedExamRequest.setLbzNurse(UUID.fromString("3e1a51ab-a3aa-1add-a3ad-28e043f8b435"));

        return schedMedExamRequest;
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
        return  jwtUtil.generateToken(claims, "45c6df92-cf4a-11ed-afa1-0242ac120002");
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
