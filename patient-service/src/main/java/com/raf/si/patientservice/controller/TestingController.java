package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.request.TestingRequest;
import com.raf.si.patientservice.dto.request.UpdateTermsNewShiftRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.service.TestingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/testing")
public class TestingController {

    private final TestingService testingService;

    public TestingController(TestingService testingService) {
        this.testingService = testingService;
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA') or hasRole('ROLE_RECEPCIONER')")
    @PostMapping("/schedule/{lbp}")
    public ResponseEntity<ScheduledTestingResponse> scheduleTesting(@PathVariable("lbp") UUID lbp,
                                                                    @RequestBody @Valid ScheduledTestingRequest request,
                                                                    @RequestHeader("Authorization") String authorizationHeader) {

        return ResponseEntity.ok(testingService.scheduleTesting(lbp, request, authorizationHeader));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA') or hasRole('ROLE_RECEPCIONER')")
    @GetMapping("/available-terms")
    public ResponseEntity<AvailableTermResponse> getAvailableTerm(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dateAndTime,
                                                                  @RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(testingService.getAvailableTerm(dateAndTime, authorizationHeader));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping("/scheduled")
    public ResponseEntity<ScheduledTestingListResponse> getScheduledTestings(@RequestParam(required = false) UUID lbp,
                                                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(testingService.getScheduledtestings(lbp, date, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PostMapping("/create/{lbp}")
    public ResponseEntity<TestingResponse> createTesting(@PathVariable("lbp") UUID lbp,
                                                         @RequestBody @Valid TestingRequest request) {

        return ResponseEntity.ok(testingService.createTesting(lbp, request));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PatchMapping("/scheduled/change-status/{scheduled-testing-id}")
    public ResponseEntity<ScheduledTestingResponse> changeTestingStatus(@PathVariable("scheduled-testing-id") Long scheduledTestingId,
                                                                        @RequestParam(required = false) String testStatus,
                                                                        @RequestParam(required = false) String patientArrivalStatus) {

        return ResponseEntity.ok(testingService.changeScheduledTestingStatus(scheduledTestingId, testStatus, patientArrivalStatus));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @DeleteMapping("/scheduled/delete/{id}")
    public ResponseEntity<ScheduledTestingResponse> deleteScheduledTesting(@PathVariable("id") Long id) {
        return ResponseEntity.ok(testingService.deleteScheduledTesting(id));
    }

    @PreAuthorize("hasRole('ROLE_VISI_LAB_TEHNICAR') or hasRole('ROLE_LAB_TEHNICAR')" +
            " or hasRole('ROLE_MED_BIOHEMICAR') or hasRole('ROLE_SPEC_MED_BIOHEMIJE')")
    @GetMapping("/scheduled/in-process")
    public ResponseEntity<TestingListResponse> processingOfTestResults(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "5") int size){
        return ResponseEntity.ok(testingService.processingOfTestResults(PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_VISI_LAB_TEHNICAR') or hasRole('ROLE_LAB_TEHNICAR')" +
            " or hasRole('ROLE_MED_BIOHEMICAR') or hasRole('ROLE_SPEC_MED_BIOHEMIJE')")
    @PatchMapping("/{id}/update-test-result")
    public ResponseEntity<TestingResponse> updateTestResult(@PathVariable("id") Long id,
                                                            @RequestParam String newTestResult) {
        return ResponseEntity.ok(testingService.updateTestResult(id, newTestResult));
    }

    @PostMapping("/update-nurse-terms")
    public ResponseEntity<List<LocalDateTime>> removeNurseFromTerms(@RequestBody UpdateTermsNewShiftRequest request) {
        return ResponseEntity.ok(testingService.removeNurseFromTerms(request));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA') or hasRole('ROLE_DR_SPEC') or hasRole('ROLE_DR_SPEC_POV') " +
            "or hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping("/history/{lbp}")
    public ResponseEntity<List<TestingResponse>> getTestingHistory(@PathVariable("lbp") UUID lbp) {
        return ResponseEntity.ok(testingService.getTestingHistory(lbp));
    }
}
