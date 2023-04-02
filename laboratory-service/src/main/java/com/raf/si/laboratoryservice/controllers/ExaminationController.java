package com.raf.si.laboratoryservice.controllers;

import com.raf.si.laboratoryservice.dto.request.CreateExaminationRequest;
import com.raf.si.laboratoryservice.dto.response.ExaminationResponse;
import com.raf.si.laboratoryservice.service.ExaminationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/examination")
public class ExaminationController {
    private final ExaminationService examinationService;

    public ExaminationController(ExaminationService examinationService) {
        this.examinationService = examinationService;
    }

    @PreAuthorize("hasRole('ROLE_VISI_LAB_TEHNICAR')" + "or hasRole('ROLE_LAB_TEHNICAR')")
    @PostMapping(path = "/create")
    public ResponseEntity<ExaminationResponse> createExamination(@Valid @RequestBody CreateExaminationRequest createExaminationRequest) {
        return ResponseEntity.ok(examinationService.createExamination(createExaminationRequest));
    }

}
