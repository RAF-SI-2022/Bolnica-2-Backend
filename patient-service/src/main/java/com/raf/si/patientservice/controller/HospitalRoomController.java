package com.raf.si.patientservice.controller;

import com.raf.si.patientservice.dto.response.HospitalBedAvailabilityResponse;
import com.raf.si.patientservice.dto.response.HospitalRoomListResponse;
import com.raf.si.patientservice.service.HospitalRoomService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/hospital-room")
public class HospitalRoomController {

    private final HospitalRoomService hospitalRoomService;

    public HospitalRoomController(HospitalRoomService hospitalRoomService) {
        this.hospitalRoomService = hospitalRoomService;
    }

    @PreAuthorize("hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping
    public ResponseEntity<HospitalRoomListResponse> getRooms(@Valid @RequestParam UUID pbo,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(hospitalRoomService.getHospitalRooms(pbo, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_VISA_MED_SESTRA') " +
            "or hasRole('ROLE_MED_SESTRA') " +
            "or hasRole('ROLE_RECEPCIONER') " +
            "or hasRole('ROLE_DR_SPEC') " +
            "or hasRole('ROLE_DR_SPEC_POV') " +
            "or hasRole('ROLE_VISI_LAB_TEHNICAR') " +
            "or hasRole('ROLE_LAB_TEHNICAR') " +
            "or hasRole('ROLE_SPEC_MED_BIOHEMIJE') " +
            "or hasRole('ROLE_MED_BIOHEMICAR') " +
            "or hasRole('ROLE_NACELNIK_ODELJENA')")
    @GetMapping("/beds")
    public ResponseEntity<HospitalBedAvailabilityResponse> getBedAvailabilityInTheRoom(@Valid @RequestParam UUID pbo) {
        return ResponseEntity.ok(hospitalRoomService.getBedAvailability(pbo));
    }
}
