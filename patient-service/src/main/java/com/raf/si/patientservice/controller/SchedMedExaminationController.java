package com.raf.si.patientservice.controller;


import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.exception.UnauthorizedException;
import com.raf.si.patientservice.service.SchedMedExaminationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.validation.Valid;
import java.util.List;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/sched-med-exam")
public class SchedMedExaminationController {

    private final SchedMedExaminationService schedMedExaminationService;

    public SchedMedExaminationController(SchedMedExaminationService schedMedExaminationService) {
        this.schedMedExaminationService = schedMedExaminationService;
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PostMapping("/create")
    public ResponseEntity<?> createSchedMedExamination(@Valid @RequestBody SchedMedExamRequest schedMedExamRequest) {
        return ResponseEntity.ok(schedMedExaminationService.createSchedMedExamination(schedMedExamRequest));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @PutMapping("/update-exam-status")
    public ResponseEntity<SchedMedExamResponse> updateSchedMedExaminationStatus(@Valid @RequestBody UpdateSchedMedExamRequest updateSchedMedExamRequest) {
        return ResponseEntity.ok(schedMedExaminationService.updateSchedMedExaminationExamStatus(updateSchedMedExamRequest));
    }


    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')" +
            "or hasRole('ROLE_MED_SESTRA')" +
            "or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping("/search/{lbz}")
    public ResponseEntity<List<SchedMedExamResponse>> getSchedMedExam(@AuthenticationPrincipal Authentication authentication
            , @PathVariable("lbz") UUID lbz, @RequestParam(name = "appointmentDate", required = false)
                                                                      @DateTimeFormat(pattern = "dd-MM-yyyy-HH:mm") Date appointmentDate) {
        if (authentication == null) {
            log.error("Pokusan neautentifikovan zahtevn");
            throw new UnauthorizedException("Morate biti prijavljeni za ovu akciju");
        }

        String token = authentication.getCredentials().toString();
        return ResponseEntity.ok(schedMedExaminationService.getSchedMEdExaminationByLbz(lbz, appointmentDate, token));
    }
        @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
        @PutMapping("/update-patient-arrival-status")
        public ResponseEntity<SchedMedExamResponse> updateSchedMedExaminationPatientArrivalStatus
        (@Valid @RequestBody UpdateSchedMedExamRequest updateSchedMedExamRequest){
            return ResponseEntity.ok(schedMedExaminationService.updateSchedMedExaminationPatientArrivalStatus(updateSchedMedExamRequest));

        }

}
