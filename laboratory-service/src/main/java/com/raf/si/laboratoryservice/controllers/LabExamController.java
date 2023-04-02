package com.raf.si.laboratoryservice.controllers;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;
import com.raf.si.laboratoryservice.service.LabExamService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/examination")
public class LabExamController {
    private final LabExamService labExamService;

    public LabExamController(LabExamService labExamService) {
        this.labExamService = labExamService;
    }

    @PreAuthorize("hasRole('ROLE_VISI_LAB_TEHNICAR')" + "or hasRole('ROLE_LAB_TEHNICAR')")
    @PostMapping(path = "/create")
    public ResponseEntity<LabExamResponse> createExamination(@Valid @RequestBody CreateLabExamRequest createLabExamRequest) {
        return ResponseEntity.ok(labExamService.createExamination(createLabExamRequest));
    }
    @PreAuthorize("hasRole('ROLE_VISI_LAB_TEHNICAR')" + "or hasRole('ROLE_LAB_TEHNICAR')")
    @GetMapping("/scheduled-count")
    public ResponseEntity<Optional<Long>> getScheduledExamCount(@RequestParam("date") Timestamp date) {
        return ResponseEntity.ok(labExamService.getScheduledExamCount(date));
    }
}