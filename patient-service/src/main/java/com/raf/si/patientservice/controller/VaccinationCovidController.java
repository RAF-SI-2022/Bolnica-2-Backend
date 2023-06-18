package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationResponse;
import com.raf.si.patientservice.service.VaccinationCovidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/vaccination")
public class VaccinationCovidController {

    private VaccinationCovidService vaccinationCovidService;

    public VaccinationCovidController(VaccinationCovidService vaccinationCovidService) {
        this.vaccinationCovidService = vaccinationCovidService;
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA') or hasRole('ROLE_RECEPCIONER')")
    @PostMapping("/schedule/{lbp}")
    public ResponseEntity<ScheduledVaccinationResponse> scheduleVaccination(@PathVariable("lbp") UUID lbp,
                                                                            @RequestBody @Valid ScheduledVaccinationRequest request,
                                                                            @RequestHeader("Authorization") String authorizationHeader) {

        return ResponseEntity.ok(vaccinationCovidService.scheduleVaccination(lbp, request, authorizationHeader));
    }
}
