package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/patient")
public class PatientController {

    @GetMapping("/token")
    public ResponseEntity<?> getTokenTest(){
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();
        return ResponseEntity.ok(tokenPayload);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/authorized")
    public ResponseEntity<?> getAuthorized(){
        return ResponseEntity.ok("Autorizovano");
    }

    @PreAuthorize("hasRole('IZMISLJENA_ROLA')")
    @GetMapping("/unauthorized")
    public ResponseEntity<?> getUnauthorized(){
        return ResponseEntity.ok("Nije autorizovano");
    }
}
