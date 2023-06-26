package com.raf.si.patientservice.integration;

import com.raf.si.patientservice.dto.request.*;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.appointment.AppointmentStatus;
import com.raf.si.patientservice.model.enums.user.Profession;
import com.raf.si.patientservice.model.enums.user.Title;
import com.raf.si.patientservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UtilsHelper {

    private final String patientJmbg = "1209217282728";
    private final UUID patientBootstrapLbp = UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8");
    private final UUID bootstrapPbo = UUID.fromString("be7fed71-9a96-4644-8d0e-f80a216f77d6");
    private final LocalDateTime patientBootStrapSchedVaccinationDate = LocalDateTime.now().plusDays(1);
    private final UUID patientWithSchedVacc = UUID.fromString("c1c8ba08-966a-4cc5-b633-d1ef15d7caaf");

    private final String vaccineBootstrap = "PRIORIX";

    private final JwtUtil jwtUtil;
    @Value("${duration.of.exam}")
    private int DURATION_OF_EXAM;
    public UtilsHelper(JwtUtil jwtUtil) {
        this.jwtUtil=jwtUtil;
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
        String[] roles= new String[]{"ROLE_VISA_MED_SESTRA","ROLE_MED_SESTRA"};
        Claims claims= Jwts.claims();

        claims.put("firstName", "Medicinska");
        claims.put("lastName","Sestra");
        claims.put("title","Med. sestra");
        claims.put("pbo",UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));
        claims.put("departmentName","Dijagnostika");
        claims.put("pbb",UUID.randomUUID());
        claims.put("hospitalName", "NoviBeograd");
        claims.put("permissions",roles);
        claims.put("covidAccess", false);
        return  jwtUtil.generateToken(claims, "3e1a51ab-a3aa-1add-a3ad-28e043f8b435");
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
        claims.put("covidAccess", false);
        return  jwtUtil.generateToken(claims, token);
    }


    public String generateToken() {
        Claims claims = Jwts.claims();
        claims.put("firstName", "admin");
        claims.put("lastName", "adminovic");
        claims.put("title", Title.DR_SCI_MED.getNotation());
        claims.put("profession", Profession.SPEC_HIRURG.getNotation());
        claims.put("pbo", bootstrapPbo);
        claims.put("departmentName", "departman");
        claims.put("pbb", UUID.fromString("be7fed71-9a96-4644-8d0e-f80a216f77d6"));
        claims.put("hospitalName", "Bolnica");
        String[] roles = new String[]{"ROLE_ADMIN", "ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC",
                "ROLE_DR_SPEC_POV", "ROLE_VISA_MED_SESTRA", "ROLE_MED_SESTRA"};
        claims.put("permissions", roles);
        claims.put("covidAccess", false);
        return jwtUtil.generateToken(claims, "5a2e71bb-e4ee-43dd-a3ad-28e043f8b435");
    }

    public String generateTokenEmployeeDoesntExist() {
        Claims claims = Jwts.claims();
        claims.put("firstName", "admin");
        claims.put("lastName", "adminovic");
        claims.put("title", Title.DR_SCI_MED.getNotation());
        claims.put("profession", Profession.SPEC_HIRURG.getNotation());
        claims.put("pbo", UUID.randomUUID());
        claims.put("departmentName", "departman");
        claims.put("pbb", UUID.fromString("be7fed71-9a96-4644-8d0e-f80a216f77d6"));
        claims.put("hospitalName", "Bolnica");
        String[] roles = new String[]{"ROLE_ADMIN", "ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC",
                "ROLE_DR_SPEC_POV", "ROLE_VISA_MED_SESTRA", "ROLE_MED_SESTRA"};
        claims.put("permissions", roles);
        claims.put("covidAccess", false);
        return jwtUtil.generateToken(claims, String.valueOf(UUID.randomUUID()));
    }

    public String generateTokenDepartmentDoesntExist() {
        Claims claims = Jwts.claims();
        claims.put("firstName", "admin");
        claims.put("lastName", "adminovic");
        claims.put("title", Title.DR_SCI_MED.getNotation());
        claims.put("profession", Profession.SPEC_HIRURG.getNotation());
        claims.put("pbo", UUID.randomUUID());
        claims.put("departmentName", "departman");
        claims.put("pbb", UUID.fromString("be7fed71-9a96-4644-8d0e-f80a216f77d6"));
        claims.put("hospitalName", "Bolnica");
        String[] roles = new String[]{"ROLE_ADMIN", "ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC",
                "ROLE_DR_SPEC_POV", "ROLE_VISA_MED_SESTRA", "ROLE_MED_SESTRA"};
        claims.put("permissions", roles);
        claims.put("covidAccess", false);
        return jwtUtil.generateToken(claims, "5a2e71bb-e4ee-43dd-a3ad-28e043f8b435");
    }

    public PatientRequest makePatientRequest() throws ParseException {
        PatientRequest patientRequest = new PatientRequest();

        patientRequest.setJmbg(patientJmbg);
        patientRequest.setFirstName("Test");
        patientRequest.setLastName("Test");
        patientRequest.setParentName("Test");
        patientRequest.setGender("Muški");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        patientRequest.setBirthDate(formatter.parse("12-12-2001"));
        patientRequest.setDeathDate(formatter.parse("12-12-2020"));
        patientRequest.setBirthplace("Test");
        patientRequest.setCitizenshipCountry("SRB");
        patientRequest.setCountryOfLiving("SRB");

        patientRequest.setAddress("Test");
        patientRequest.setPlaceOfLiving("Test");
        patientRequest.setPhoneNumber("0601234567");
        patientRequest.setEmail("test@gmail.com");
        patientRequest.setCustodianJmbg("0192023930");
        patientRequest.setCustodianName("Test Testovic");
        patientRequest.setProfession("Test");
        patientRequest.setChildrenNum(0);
        patientRequest.setEducation("Osnovno obrazovanje");
        patientRequest.setMaritalStatus("Razveden");
        patientRequest.setFamilyStatus("Usvojen");

        return patientRequest;
    }

    public SchedMedExamRequest createSchedMedExamRequest(int validDate){
        SchedMedExamRequest schedMedExamRequest= new SchedMedExamRequest();
        schedMedExamRequest.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        schedMedExamRequest.setLbzDoctor(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        schedMedExamRequest.setAppointmentDate(new Date(System.currentTimeMillis() + 1000000000L * validDate));
        schedMedExamRequest.setLbzNurse(UUID.fromString("3e1a51ab-a3aa-1add-a3ad-28e043f8b435"));

        return schedMedExamRequest;
    }

    public UpdateSchedMedExamRequest createUpdateSchedMedExamRequest(String status) {
        UpdateSchedMedExamRequest updateSchedMedExamRequest= new UpdateSchedMedExamRequest();

        updateSchedMedExamRequest.setId(1L);
        updateSchedMedExamRequest.setNewStatus(status);

        return updateSchedMedExamRequest;
    }

    public ScheduledMedExamination createSchedMedExamination(Patient patient) {
        ScheduledMedExamination scheduledMedExamination= new ScheduledMedExamination();
        scheduledMedExamination.setPatient(patient);
        scheduledMedExamination.setLbzDoctor(UUID.fromString("266a1e0c-cf45-11ed-afa1-0242ac120002"));
        scheduledMedExamination.setAppointmentDate(new Date(new Date().getTime() + (DURATION_OF_EXAM + 5 ) * 60 * 1000));
        scheduledMedExamination.setLbzNurse(UUID.fromString("3e1a51ab-a3aa-1add-a3ad-28e043f8b435"));
        return scheduledMedExamination;
    }


    public HospitalizationRequest makeHospitalizationRequest() {
        HospitalizationRequest request = new HospitalizationRequest();
        long id = 1;

        request.setLbp(patientBootstrapLbp);
        request.setReferralId(id);
        request.setDiagnosis("Dijagnoza");
        request.setSpecialistLbz(UUID.fromString("266a1e0c-cf45-11ed-afa1-0242ac120002"));
        request.setNote("Napomena");
        request.setHospitalRoomId(id);

        return request;
    }

    public CreateAppointmentRequest makeCreateAppointmentRequest() {
        CreateAppointmentRequest request = new CreateAppointmentRequest();

        request.setLbp(patientBootstrapLbp);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            request.setReceiptDate(formatter.parse("12-12-2050"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return request;
    }

    public Appointment makeAppointment() {
        Appointment appointment = new Appointment();

        appointment.setEmployeeLBZ(UUID.fromString("266a1e0c-cf45-11ed-afa1-0242ac120002"));
        appointment.setPbo(bootstrapPbo);
        appointment.setStatus(AppointmentStatus.ZAKAZAN);
        appointment.setReceiptDate(new Date());

        return appointment;
    }

    public String getPatientJmbg(){
        return patientJmbg;
    }

    public UUID getPatientBootstrapLbp(){
        return patientBootstrapLbp;
    }

    public UUID getBootstrapPbo() {
        return bootstrapPbo;
    }

    public String getVaccineBootstrap(){
        return vaccineBootstrap;
    }

    public LocalDateTime getPatientBootStrapSchedVaccinationDate(){ return patientBootStrapSchedVaccinationDate;}

    public UUID getPatientWithSchedVacc() {
        return patientWithSchedVacc;
    }

    public Date makeDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.parse(date);
    }

    public ScheduledVaccinationRequest makeSchedVaccinationCovidRequest() {
        ScheduledVaccinationRequest request = new ScheduledVaccinationRequest();
        request.setDateAndTime(LocalDateTime.now().plusHours(1));
        request.setNote("notes");
        return request;
    }

    public String generateNurseTokenForValidPbo(){
        String[] roles= new String[]{"ROLE_VISA_MED_SESTRA","ROLE_MED_SESTRA"};
        Claims claims= Jwts.claims();

        claims.put("firstName", "Medicinska");
        claims.put("lastName","Sestra");
        claims.put("title","Med. sestra");
        claims.put("pbo",UUID.fromString("8c9169e8-01ff-4172-b537-9e816f102750"));
        claims.put("departmentName","Dijagnostika");
        claims.put("pbb",UUID.randomUUID());
        claims.put("hospitalName", "NoviBeograd");
        claims.put("permissions",roles);
        claims.put("covidAccess", false);
        return  jwtUtil.generateToken(claims, "3e1a51ab-a3aa-1add-a3ad-28e043f8b435");
    }

    public VaccinationCovidRequest makeVaccinationCovidRequest(){
        VaccinationCovidRequest request = new VaccinationCovidRequest();
        request.setVaccinationId(2L);
        request.setVaccineName("Pfizer");
        request.setDateTime(LocalDateTime.now());
        request.setDoseReceived(0L);

        return  request;
    }
    public ScheduledVaccinationCovid makeScheduledVaccinationCovid(Patient patient){

        ScheduledVaccinationCovid scheduledVaccinationCovid = new ScheduledVaccinationCovid();
        scheduledVaccinationCovid.setPatient(patient);
        LocalDateTime schedVaccDate = LocalDateTime.now().plusDays(3);
        scheduledVaccinationCovid.setDateAndTime(schedVaccDate);
        scheduledVaccinationCovid.setNote("");
        scheduledVaccinationCovid.setSchedulerLbz(UUID.randomUUID());

        AvailableTerm availableTerm = new AvailableTerm();
        availableTerm.setDateAndTime(schedVaccDate);
        availableTerm.setPbo(UUID.randomUUID());
        availableTerm.setAvailableNursesNum(1);
        availableTerm.setScheduledTermsNum(1);
        availableTerm.setScheduledVaccinationCovids(new ArrayList<>());
        availableTerm.setScheduledTestings(new ArrayList<>());

        scheduledVaccinationCovid.setAvailableTerm(availableTerm);
        availableTerm.addScheduledVaccination(scheduledVaccinationCovid);

        return scheduledVaccinationCovid;
    }

}
