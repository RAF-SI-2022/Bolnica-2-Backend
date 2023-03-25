package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.service.HealthRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/record")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    public HealthRecordController(HealthRecordService healthRecordService) {
        this.healthRecordService = healthRecordService;
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping("/{lbp}")
    public ResponseEntity<HealthRecordResponse> getHealthRecordForPatient(@PathVariable("lbp") UUID lbp){
        return ResponseEntity.ok(healthRecordService.getHealthRecordForPatient(lbp));
    }
}
