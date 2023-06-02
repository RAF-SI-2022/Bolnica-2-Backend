package com.raf.si.laboratoryservice.cucumber;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralType;
import com.raf.si.laboratoryservice.model.enums.user.Profession;
import com.raf.si.laboratoryservice.model.enums.user.Title;
import com.raf.si.laboratoryservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class UtilsHelper {
    private final JwtUtil jwtUtil;

    public UtilsHelper(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public CreateReferralRequest createReferralRequest() {
        CreateReferralRequest createReferralRequest = new CreateReferralRequest();
        createReferralRequest.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        createReferralRequest.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        createReferralRequest.setType("Laboratorija");
        createReferralRequest.setReferralDiagnosis("Mononukleoza");
        createReferralRequest.setReferralReason("Provera krvne slike pacijenta, da li je mononukleoza prosla");
        createReferralRequest.setPboReferredFrom(UUID.fromString("d79f77be-0a0e-4e2f-88a5-5f5d5cdd1e2c"));
        createReferralRequest.setPboReferredTo(UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));
        createReferralRequest.setCreationTime(new Date(2023-04-03));
        return createReferralRequest;
    }

    public CreateLabExamRequest createLabExamRequest() {
        CreateLabExamRequest createLabExamRequest = new CreateLabExamRequest();
        createLabExamRequest.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        createLabExamRequest.setScheduledDate(new Date());
        createLabExamRequest.setNote("Napomena");
        return createLabExamRequest;
    }

    public String getDate() {
        Date currentDate = DateUtils.addDays(new Date(), 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(currentDate);
    }



    public String generateToken() {
        Claims claims = Jwts.claims();
        claims.put("firstName", "admin");
        claims.put("lastName", "adminovic");
        claims.put("title", Title.DR_SCI_MED.getNotation());
        claims.put("profession", Profession.SPEC_HIRURG.getNotation());
        claims.put("pbo", "4e5911c8-ce7a-11ed-afa1-0242ac120002");
        claims.put("departmentName", "departman");
        claims.put("pbb", UUID.randomUUID());
        claims.put("hospitalName", "Bolnica");
        String[] roles = new String[]{"ROLE_ADMIN", "ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC",
                "ROLE_DR_SPEC_POV", "ROLE_VISA_MED_SESTRA", "ROLE_MED_SESTRA", "ROLE_VISI_LAB_TEHNICAR", "ROLE_LAB_TEHNICAR"};
        claims.put("permissions", roles);
        claims.put("covidAccess", false);
        return jwtUtil.generateToken(claims, "5a2e71bb-e4ee-43dd-a3ad-28e043f8b435");
    }
}
