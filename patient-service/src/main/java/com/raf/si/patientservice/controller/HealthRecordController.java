package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.*;
import com.raf.si.patientservice.dto.response.*;
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
            "or hasRole('ROLE_DR_SPEC_POV')" +
            "or hasRole('ROLE_MED_SESTRA')" +
            "or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping("/{lbp}")
    public ResponseEntity<HealthRecordResponse> getHealthRecordForPatient(@PathVariable("lbp") UUID lbp,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "5") int size){

        return ResponseEntity.ok(healthRecordService.getHealthRecordForPatient(lbp, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping("/allergens")
    public ResponseEntity<AllergenListResponse> getAvailableAllergens(){

        return ResponseEntity.ok(healthRecordService.getAvailableAllergens());
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping("/vaccines")
    public ResponseEntity<VaccineListResponse> getAvailableVaccines(){

        return ResponseEntity.ok(healthRecordService.getAvailableVaccines());
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @PutMapping("/{lbp}")
    public ResponseEntity<BasicHealthRecordResponse> updateHealthRecord(@PathVariable("lbp") UUID lbp,
                                                              @Valid @RequestBody UpdateHealthRecordRequest updateHealthRecordRequest){

        return ResponseEntity.ok(healthRecordService.updateHealthRecord(updateHealthRecordRequest, lbp));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @PutMapping("add-alergy/{lbp}")
    public ResponseEntity<ExtendedAllergyResponse> addAlergy(@PathVariable("lbp") UUID lbp,
                                                     @Valid @RequestBody AddAllergyRequest addAllergyRequest){

        return ResponseEntity.ok(healthRecordService.addAllergy(addAllergyRequest, lbp));
    }


    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @PutMapping("add-vaccination/{lbp}")
    public ResponseEntity<ExtendedVaccinationResponse> addVaccination(@PathVariable("lbp") UUID lbp,
                                                                      @Valid @RequestBody AddVaccinationRequest addVaccinationRequest){

        return ResponseEntity.ok(healthRecordService.addVaccination(addVaccinationRequest, lbp));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @PostMapping("create-examination-report/{lbp}")
    public ResponseEntity<MessageResponse> createExaminationReport(@PathVariable("lbp") UUID lbp,
                                                          @RequestParam UUID lbz,
                                                          @Valid @RequestBody CreateExaminationReportRequest createExaminationReportRequest){

        return ResponseEntity.ok(healthRecordService.createExaminationReportRequest(lbp, lbz, createExaminationReportRequest));
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
