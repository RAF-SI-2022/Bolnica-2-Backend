package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.MedicalExaminationFilterRequest;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.LightHealthRecordResponse;
import com.raf.si.patientservice.dto.response.MedicalExaminationListResponse;
import com.raf.si.patientservice.dto.response.MedicalHistoryListResponse;
import com.raf.si.patientservice.service.HealthRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/record")
public class HealthRecordController{

    private final HealthRecordService healthRecordService;

    public HealthRecordController(HealthRecordService healthRecordService) {
        this.healthRecordService = healthRecordService;
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping("/{lbp}")
    public ResponseEntity<HealthRecordResponse> getHealthRecordForPatient(@PathVariable("lbp") UUID lbp,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "5") int size){

        return ResponseEntity.ok(healthRecordService.getHealthRecordForPatient(lbp, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping("/light/{lbp}")
    public ResponseEntity<LightHealthRecordResponse> getLightHealthRecordResponse(@PathVariable("lbp") UUID lbp,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "5") int size){

        return ResponseEntity.ok(healthRecordService.getLightHealthRecordForPatient(lbp, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @PostMapping("/examinations/{lbp}")
    public ResponseEntity<MedicalExaminationListResponse> getExaminations(@PathVariable("lbp") UUID lbp,
                                                                          @Valid @RequestBody MedicalExaminationFilterRequest request,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "5") int size){

        return ResponseEntity.ok(healthRecordService.findExaminations(lbp, request, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping("/history/{lbp}")
    public ResponseEntity<MedicalHistoryListResponse> getMedicalHistory(@PathVariable("lbp") UUID lbp,
                                                                        @RequestParam(required = false) String mkb10,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "5") int size){

        return ResponseEntity.ok(healthRecordService.findMedicalHistory(lbp, mkb10, PageRequest.of(page, size)));
    }
}
