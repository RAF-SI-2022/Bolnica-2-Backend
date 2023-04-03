package com.raf.si.laboratoryservice.cucumber;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralType;
import com.raf.si.laboratoryservice.model.enums.user.Profession;
import com.raf.si.laboratoryservice.model.enums.user.Title;
import com.raf.si.laboratoryservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.sql.Timestamp;
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
        createReferralRequest.setType(ReferralType.LABORATORIJA);
        createReferralRequest.setReferralDiagnosis("Mononukleoza");
        createReferralRequest.setReferralReason("Provera krvne slike pacijenta, da li je mononukleoza prosla");
        createReferralRequest.setPboReferredFrom(UUID.fromString("d79f77be-0a0e-4e2f-88a5-5f5d5cdd1e2c"));
        createReferralRequest.setPboReferredTo(UUID.fromString("a3070ae9-dcd2-4122-8138-b0d6f3193b10"));
        createReferralRequest.setCreationTime(new Timestamp(2023-04-03));

        return createReferralRequest;
    }

    public String getToken() {
        return "eyJhbGciOiJIUzUxMiJ9.eyJmaXJzdE5hbWUiOiJBZG1pbiIsImxhc3ROYW1lIjoiQWRtaW5vdmljIiwidGl0bGUiOiJEciBzY2kuIG1lZC4iLCJwcm9mZXNzaW9uIjoiU3BlYy4gaGlydXJnIiwicGJvIjoiYTMwNzBhZTktZGNkMi00MTIyLTgxMzgtYjBkNmYzMTkzYjEwIiwiZGVwYXJ0bWVudE5hbWUiOiJIaXJ1cmdpamEiLCJwYmIiOiJjOWE0ZmUzMC00YmQwLTQwMzAtOTM3OS0wZmRjZWEzZjc3NGQiLCJob3NwaXRhbE5hbWUiOiJLQkMgWnZlemRhcmEgLSBLbGluaWthIHphIGhpcnVyZ2lqdSBcIk5pa29sYSBTcGFzaWNcIiIsInBlcm1pc3Npb25zIjpbIlJPTEVfVklTQV9NRURfU0VTVFJBIiwiUk9MRV9NRURfU0VTVFJBIiwiUk9MRV9BRE1JTiIsIlJPTEVfRFJfU1BFQ19PREVMSkVOSkEiLCJST0xFX0RSX1NQRUMiLCJST0xFX0RSX1NQRUNfUE9WIiwiUk9MRV9WSVNJX0xBQl9URUhOSUNBUiIsIlJPTEVfTEFCX1RFSE5JQ0FSIiwiUk9MRV9NRURfQklPSEVNSUNBUiIsIlJPTEVfU1BFQ19NRURfQklPSEVNSUpFIl0sInN1YiI6IjVhMmU3MWJiLWU0ZWUtNDNkZC1hM2FkLTI4ZTA0M2Y4YjQzNSIsImlhdCI6MTY4MDUxODA5MCwiZXhwIjoxNjgwNTU0MDkwfQ.5xrclb4BKoYP8nynsKhYANPgawDbGfJ9Jm3jBYDbC1jlfrI6YU6B1BMjlMVLk487BFg1FogmcsyvpNQB8zNbqw";
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
}
