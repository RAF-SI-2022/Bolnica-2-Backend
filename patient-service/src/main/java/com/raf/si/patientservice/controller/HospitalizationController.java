package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.response.HospitalizationResponse;
import com.raf.si.patientservice.service.HospitalizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/hospitalization")
public class HospitalizationController {

    private final HospitalizationService hospitalizationService;

    public HospitalizationController(HospitalizationService hospitalizationService) {
        this.hospitalizationService = hospitalizationService;
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PostMapping("/hospitalize")
    public ResponseEntity<HospitalizationResponse> hospitalization(@Valid @RequestBody HospitalizationRequest request,
                                                                   @RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(hospitalizationService.hospitalize(request, authorizationHeader));
    }
}
