package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.VisitRequest;
import com.raf.si.patientservice.dto.response.VisitListResponse;
import com.raf.si.patientservice.dto.response.VisitResponse;
import com.raf.si.patientservice.service.VisitService;
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
@RequestMapping("/visits")
public class VisitController {

    private final VisitService visitService;


    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA') or hasRole('ROLE_RECEPCIONER')")
    @PostMapping("/{lbp}")
    public ResponseEntity<VisitResponse> createVisit(@PathVariable("lbp") UUID lbp,
                                                     @Valid @RequestBody VisitRequest visitRequest) {
        return ResponseEntity.ok(visitService.createVisit(lbp, visitRequest));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA') or hasRole('ROLE_DR_SPEC') or hasRole('ROLE_DR_SPEC_POV') " +
            "or hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping("/{lbp}")
    public ResponseEntity<VisitListResponse> getVisitsForPatient(@PathVariable("lbp") UUID lbp,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(visitService.getVisits(lbp, PageRequest.of(page, size)));
    }
}
