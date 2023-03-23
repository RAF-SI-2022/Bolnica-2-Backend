package com.raf.si.patientservice.controller;


import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.PatientResponse;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.service.SchedMedExaminationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/schedMedExam")
public class SchedMedExaminationController {

    private final SchedMedExaminationService schedMedExaminationService;

    public SchedMedExaminationController(SchedMedExaminationService schedMedExaminationService) {
        this.schedMedExaminationService = schedMedExaminationService;
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PostMapping("/create")
    public ResponseEntity<?> createSchedMedExamination(@Valid @RequestBody SchedMedExamRequest schedMedExamRequest){
        return ResponseEntity.ok(schedMedExaminationService.createSchedMedExamination(schedMedExamRequest));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @DeleteMapping("/update")
    public ResponseEntity<SchedMedExamResponse> updateSchedMedExamination(@Valid @RequestBody UpdateSchedMedExamRequest updateSchedMedExamRequest){
        return ResponseEntity.ok(schedMedExaminationService.updateSchedMedExamination(updateSchedMedExamRequest));
    }

}
