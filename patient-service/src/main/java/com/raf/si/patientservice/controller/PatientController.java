package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
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
import java.util.UUID;

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
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientRequest patientRequest){
        return ResponseEntity.ok(patientService.createPatient(patientRequest));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PutMapping("/update")
    public ResponseEntity<PatientResponse> updatePatientByJmbg(@Valid @RequestBody PatientRequest patientRequest){
        return ResponseEntity.ok(patientService.updatePatientByJmbg(patientRequest));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PutMapping("/update/{lbp}")
    public ResponseEntity<PatientResponse> updatePatientByJmbg(@Valid @RequestBody PatientRequest patientRequest,
                                                               @PathVariable("lbp") UUID lbp){
        return ResponseEntity.ok(patientService.updatePatientByLbp(patientRequest, lbp));
    }

    @PreAuthorize("hasRole('ROLE_VISA_MED_SESTRA')")
    @DeleteMapping("/delete/{lbp}")
    public ResponseEntity<PatientResponse> deletePatient(@PathVariable("lbp") UUID lbp){
        return ResponseEntity.ok(patientService.deletePatient(lbp));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')" +
            "or hasRole('ROLE_MED_SESTRA')" +
            "or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping("/{lbp}")
    public ResponseEntity<PatientResponse> getPatientByLbp(@PathVariable("lbp") UUID lbp){
        return ResponseEntity.ok(patientService.getPatientByLbp(lbp));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping("/record/{lbp}")
    public ResponseEntity<HealthRecordResponse> getHealthRecordForPatient(@PathVariable("lbp") UUID lbp){
        return ResponseEntity.ok(patientService.getHealthRecordForPatient(lbp));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/token")
    public ResponseEntity<?> getToken(){
        return ResponseEntity.ok(TokenPayloadUtil.getTokenPayload());
    }
}
