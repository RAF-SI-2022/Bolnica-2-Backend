package com.raf.si.laboratoryservice.controllers;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import com.raf.si.laboratoryservice.service.ReferralService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/referral")
public class ReferralController {

    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

//    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" + "or hasRole('ROLE_DR_SPEC')" + "or hasRole('ROLE_DR_SPEC_POV')")
    @PostMapping(path = "/create")
    public ResponseEntity<ReferralResponse> createReferral(@RequestBody CreateReferralRequest createReferralRequest) {
        return ResponseEntity.ok(referralService.createReferral(createReferralRequest));
    }

    //    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA')" + "or hasRole('ROLE_DR_SPEC')" + "or hasRole('ROLE_DR_SPEC_POV')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ReferralResponse> getReferral(@PathVariable Long id) {
        return ResponseEntity.ok(referralService.getReferral(id));
    }

}
