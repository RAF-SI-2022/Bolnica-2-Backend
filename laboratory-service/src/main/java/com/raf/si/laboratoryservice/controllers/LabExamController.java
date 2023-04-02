package com.raf.si.laboratoryservice.controllers;

import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.response.LabExamResponse;
import com.raf.si.laboratoryservice.service.LabExamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/examination")
public class LabExamController {
    private final LabExamService labExamService;

    public LabExamController(LabExamService labExamService) {
        this.labExamService = labExamService;
    }

    @PreAuthorize("hasRole('ROLE_VISI_LAB_TEHNICAR')" + "or hasRole('ROLE_LAB_TEHNICAR')")
    @PostMapping(path = "/create")
    public ResponseEntity<LabExamResponse> createExamination(@Valid @RequestBody CreateLabExamRequest createLabExamRequest) {
        return ResponseEntity.ok(labExamService.createExamination(createLabExamRequest));
    }

//    @RestController
//    @RequestMapping("/lab-exams")
//    public class LabExamController {
//
//        @Autowired
//        private LabExamService labExamService;
//
//        @GetMapping("/scheduled-count")
//        public ResponseEntity<Integer> getScheduledExamCount(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            Long departmentId = extractDepartmentIdFromToken(authentication);
//
//            int scheduledCount = labExamService.getScheduledExamCountForDepartmentAndDate(departmentId, date);
//            return ResponseEntity.ok(scheduledCount);
//        }
//
//        private Long extractDepartmentIdFromToken(Authentication authentication) {
//            // Code to extract department ID from authentication token goes here
//        }
//    }


}
