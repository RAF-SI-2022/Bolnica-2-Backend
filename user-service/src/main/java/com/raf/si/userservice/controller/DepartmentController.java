package com.raf.si.userservice.controller;

import com.raf.si.userservice.dto.response.DepartmentResponse;
import com.raf.si.userservice.dto.response.HospitalResponse;
import com.raf.si.userservice.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/departments")
public class DepartmentController {

    private final DepartmentService departmentService;


    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA') or hasRole('ROLE_DR_SPEC') or " +
            "hasRole('ROLE_DR_SPEC_POV') or hasRole('ROLE_VISA_MED_SESTRA') or " +
            "hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_RECEPCIONER')")
    @GetMapping("/{pbb}")
    public ResponseEntity<List<DepartmentResponse>> getDepartmentsByHospital(@PathVariable("pbb") UUID pbb) {
        return ResponseEntity.ok(departmentService.getDepartmentsByHospital(pbb));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/hospitals")
    public ResponseEntity<List<HospitalResponse>> getHospitals() {
        return ResponseEntity.ok(departmentService.getAllHospitals());
    }
}
