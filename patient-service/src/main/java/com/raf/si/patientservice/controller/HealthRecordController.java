package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.AddAllergyRequest;
import com.raf.si.patientservice.dto.request.AddVaccinationRequest;
import com.raf.si.patientservice.dto.request.MedicalExaminationFilterRequest;
import com.raf.si.patientservice.dto.request.UpdateHealthRecordRequest;
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
    public ResponseEntity<MessageResponse> updateHealthRecord(@PathVariable("lbp") UUID lbp,
                                                              @Valid @RequestBody UpdateHealthRecordRequest updateHealthRecordRequest){

        return ResponseEntity.ok(healthRecordService.updateHealthrecord(updateHealthRecordRequest, lbp));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @PutMapping("add_alergy/{lbp}")
    public ResponseEntity<MessageResponse> addAlergy(@PathVariable("lbp") UUID lbp,
                                                     @Valid @RequestBody AddAllergyRequest addAllergyRequest){

        return ResponseEntity.ok(healthRecordService.addAllergy(addAllergyRequest, lbp));
    }


    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @PutMapping("add_vaccination/{lbp}")
    public ResponseEntity<MessageResponse> addVaccination(@PathVariable("lbp") UUID lbp,
                                                     @Valid @RequestBody AddVaccinationRequest addVaccinationRequest){

        return ResponseEntity.ok(healthRecordService.addVaccination(addVaccinationRequest, lbp));
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
