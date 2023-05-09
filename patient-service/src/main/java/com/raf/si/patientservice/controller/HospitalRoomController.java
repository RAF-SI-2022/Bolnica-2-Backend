package com.raf.si.patientservice.controller;

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
}
