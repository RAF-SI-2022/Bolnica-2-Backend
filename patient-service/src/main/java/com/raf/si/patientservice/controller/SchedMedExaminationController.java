package com.raf.si.patientservice.controller;


import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.TimeRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateTermsNewShiftRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamListResponse;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.service.SchedMedExaminationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
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

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')" +
            " or hasRole('ROLE_RECEPCIONER')")
    @PostMapping("/create")
    public ResponseEntity<SchedMedExamResponse> createSchedMedExamination(@Valid @RequestBody SchedMedExamRequest schedMedExamRequest,
                                                       @RequestHeader("Authorization") String authorizationHeader) {
        return ResponseEntity.ok(schedMedExaminationService.createSchedMedExamination(schedMedExamRequest, authorizationHeader));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')")
    @PutMapping("/update-exam-status")
    public ResponseEntity<SchedMedExamResponse> updateSchedMedExaminationStatus(@Valid @RequestBody UpdateSchedMedExamRequest updateSchedMedExamRequest) {
        return ResponseEntity.ok(schedMedExaminationService.updateSchedMedExaminationExamStatus(updateSchedMedExamRequest));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA') or hasRole('ROLE_RECEPCIONER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SchedMedExamResponse> deleteSchedMedExamination(@PathVariable("id") Long id){
        return ResponseEntity.ok(schedMedExaminationService.deleteSchedMedExamination(id));

    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" +
            "or hasRole('ROLE_DR_SPEC')" +
            "or hasRole('ROLE_DR_SPEC_POV')" +
            "or hasRole('ROLE_MED_SESTRA')" +
            "or hasRole('ROLE_VISA_MED_SESTRA')" +
            "or hasRole('ROLE_RECEPCIONER')")
    @GetMapping("/search")
    public ResponseEntity<SchedMedExamListResponse> getSchedMedExam(@RequestHeader("Authorization") String authorizationHeader,
                                                                    @RequestParam("lbz") UUID lbz,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size,
                                                                    @RequestParam(name = "appointmentDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date appointmentDate) {
        return ResponseEntity.ok(schedMedExaminationService.getSchedMedExaminationByLbz(lbz, appointmentDate, authorizationHeader, PageRequest.of(page,size) ));
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PutMapping("/update-patient-arrival-status")
    public ResponseEntity<SchedMedExamResponse> updateSchedMedExaminationPatientArrivalStatus
    (@Valid @RequestBody UpdateSchedMedExamRequest updateSchedMedExamRequest){
        return ResponseEntity.ok(schedMedExaminationService.updateSchedMedExaminationPatientArrivalStatus(updateSchedMedExamRequest));

    }

    @PostMapping("/{lbz}/has-for-timeslot")
    public ResponseEntity<List<Date>> doctorHasScheduledExamsForTimeSlot(@PathVariable("lbz") UUID lbz,
                                                                         @RequestBody UpdateTermsNewShiftRequest request) {
        return ResponseEntity.ok(schedMedExaminationService.doctorHasScheduledExamsForTimeSlot(lbz, request));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC') or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping("/covid")
    public ResponseEntity<SchedMedExamListResponse> getCovidScheduledExams(@RequestParam(name = "lbp", required = false) UUID lbp,
                                                                           @RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") Date date,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(schedMedExaminationService.findCovidSchedMedExams(lbp, date, PageRequest.of(page, size)));
    }
}
