package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.request.VaccinationCovidRequest;
import com.raf.si.patientservice.dto.response.DosageReceivedResponse;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationListResponse;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationResponse;
import com.raf.si.patientservice.dto.response.VaccinationCovidResponse;
import com.raf.si.patientservice.service.VaccinationCovidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
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

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping("/scheduled")
    public ResponseEntity<ScheduledVaccinationListResponse> getScheduledVaccinations(@RequestParam(required = false) UUID lbp,
                                                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                                                     @RequestParam(defaultValue = "0") int page,
                                                                                     @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(vaccinationCovidService.getScheduledVaccinations(lbp, date, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PostMapping("/create/{lbp}")
    public  ResponseEntity<VaccinationCovidResponse> createVaccination(@RequestHeader("Authorization") String authorizationHeader
                                                                        , @RequestBody @Valid VaccinationCovidRequest request
                                                                        , @PathVariable("lbp") UUID lbp){
        return  ResponseEntity.ok(vaccinationCovidService.createVaccination(lbp, request, authorizationHeader));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping("/received-dosage/{lbp}")
    public ResponseEntity<DosageReceivedResponse> getPatientDosageReceived(@PathVariable("lbp") UUID lbp){
        return ResponseEntity.ok(vaccinationCovidService.getPatientDosageReceived(lbp));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PatchMapping("/scheduled/change-status/{scheduled-vaccination-id}")
    public ResponseEntity<ScheduledVaccinationResponse> changeVaccinationStatus(@PathVariable("scheduled-vaccination-id") Long scheduledVaccinationId,
                                                                        @RequestParam(required = false) String vaccStatus,
                                                                        @RequestParam(required = false) String patientArrivalStatus) {

        return ResponseEntity.ok(vaccinationCovidService.changeScheduledVaccinationStatus(scheduledVaccinationId, vaccStatus, patientArrivalStatus));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @DeleteMapping("/scheduled/delete/{id}")
    public ResponseEntity<ScheduledVaccinationResponse> deleteScheduledVaccination(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vaccinationCovidService.deleteScheduledVaccination(id));
    }

}
