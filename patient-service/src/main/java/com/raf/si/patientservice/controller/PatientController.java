package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.PatientResponse;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PostMapping("/create")
    public ResponseEntity<?> createPatient(@Valid @RequestBody PatientRequest patientRequest){
        return ResponseEntity.ok(patientService.createPatient(patientRequest));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/token")
    public ResponseEntity<?> getToken(){
        return ResponseEntity.ok(TokenPayloadUtil.getTokenPayload());
    }
}
