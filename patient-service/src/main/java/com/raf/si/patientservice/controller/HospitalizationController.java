package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.response.HospitalisedPatientsListResponse;
import com.raf.si.patientservice.dto.response.HospitalizationResponse;
import com.raf.si.patientservice.service.HospitalizationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

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

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA') or hasRole('ROLE_DR_SPEC') or hasRole('ROLE_DR_SPEC_POV') " +
            "or hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping("/{pbo}")
    public ResponseEntity<HospitalisedPatientsListResponse> getHospitalisedPatients(@PathVariable("pbo") UUID pbo,
                                                                                    @RequestParam(name = "lbp", required = false) UUID lbp,
                                                                                    @RequestParam(name = "firstName", required = false) String firstName,
                                                                                    @RequestParam(name = "lastName", required = false) String lastName,
                                                                                    @RequestParam(name = "jmbg", required = false) String jmbg,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "5") int size,
                                                                                    @RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(
                hospitalizationService.getHospitalisedPatients(
                        authorizationHeader, pbo, lbp, firstName, lastName, jmbg, PageRequest.of(page, size)
                )
        );
    }
}
