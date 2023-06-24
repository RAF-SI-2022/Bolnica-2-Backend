package com.raf.si.userservice.controller;

import com.raf.si.userservice.dto.request.*;
import com.raf.si.userservice.dto.response.*;
import com.raf.si.userservice.exception.ForbiddenException;
import com.raf.si.userservice.service.UserService;
import com.raf.si.userservice.utils.TokenPayload;
import com.raf.si.userservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }

    @GetMapping("/{lbz}")
    public ResponseEntity<UserResponse> getUserByLbz(@PathVariable("lbz") UUID lbz) {
        TokenPayload payload = TokenPayloadUtil.getTokenPayload();

        if (!payload.getPermissions().contains("ROLE_ADMIN") && !lbz.equals(payload.getLbz())) {
            log.error("LBZ nije isti za korisnika sa lbz-om '{}' i trazeni lbz '{}'", lbz, payload.getLbz());
            throw new ForbiddenException("Nemate permisije za ovu akciju");
        }

        return ResponseEntity.ok(userService.getUserByLbz(lbz));
    }

    @GetMapping("/employee-info/{lbz}")
    public ResponseEntity<UserResponse> getEmployeeInfo(@PathVariable("lbz") UUID lbz) {
        return ResponseEntity.ok(userService.getUserByLbz(lbz));
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA') or hasRole('ROLE_DR_SPEC') or " +
            "hasRole('ROLE_DR_SPEC_POV') or hasRole('ROLE_VISA_MED_SESTRA') or " +
            "hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_RECEPCIONER') or " +
            "hasRole('ROLE_VISI_LAB_TEHNICAR') or hasRole('ROLE_LAB_TEHNICAR')")
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return ResponseEntity.ok(userService.getAllDoctors());
    }

    @PreAuthorize("hasRole('ROLE_DR_SPEC_ODELJENJA') or hasRole('ROLE_DR_SPEC') or " +
            "hasRole('ROLE_DR_SPEC_POV') or hasRole('ROLE_VISA_MED_SESTRA') or " +
            "hasRole('ROLE_MED_SESTRA') or hasRole('ROLE_RECEPCIONER')")
    @GetMapping("/doctors/{pbo}")
    public ResponseEntity<List<DoctorResponse>> getAllDoctorsByDepartment(@PathVariable("pbo") UUID pbo) {
        return ResponseEntity.ok(userService.getAllDoctorsByDepartment(pbo));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable("id") Long id) {
        TokenPayload payload = TokenPayloadUtil.getTokenPayload();
        return ResponseEntity.ok(userService.deleteUser(id, payload.getLbz()));
    }

    @PutMapping("/{lbz}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("lbz") UUID lbz,
                                                   @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        TokenPayload payload = TokenPayloadUtil.getTokenPayload();
        boolean isAdmin = payload.getPermissions().contains("ROLE_ADMIN");


        if (!isAdmin && !lbz.equals(payload.getLbz())) {
            log.error("LBZ nije isti za korisnika sa lbz-om '{}' i trazeni lbz '{}'", lbz, payload.getLbz());
            throw new ForbiddenException("Nemate permisije za ovu akciju");
        }

        return ResponseEntity.ok(userService.updateUser(lbz, updateUserRequest, isAdmin));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<UserListAndCountResponse> listUsers(@RequestParam(defaultValue = "") String firstName,
                                                              @RequestParam(defaultValue = "") String lastName,
                                                              @RequestParam(defaultValue = "") String departmentName,
                                                              @RequestParam(defaultValue = "") String hospitalName,
                                                              @RequestParam(defaultValue = "false") boolean includeDeleted,
                                                              @RequestParam(required = false) Boolean covidAccess,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(userService.listUsers(firstName, lastName, departmentName, hospitalName, includeDeleted, covidAccess, PageRequest.of(page, size)));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        return ResponseEntity.ok(userService.resetPassword(passwordResetRequest));
    }

    @PostMapping("/update-password")
    public ResponseEntity<MessageResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        return ResponseEntity.ok(userService.updatePassword(updatePasswordRequest));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DR_SPEC_ODELJENJA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PutMapping("/update-covid-access/{lbz}")
    public ResponseEntity<UserResponse> updateCovidAccess(@PathVariable("lbz") UUID lbz,
                                                          @RequestParam("covidAccess") Boolean covidAccess) {

        return ResponseEntity.ok(userService.updateCovidAccess(lbz, covidAccess));
    }

    @PostMapping("/lbz/list")
    public ResponseEntity<List<UserResponse>> getUsersByLbzList(@RequestBody UUIDListRequest request) {
        return ResponseEntity.ok(userService.getUsersByLbzList(request));
    }

    @GetMapping("/head-department/{pbo}")
    public ResponseEntity<DoctorResponse> getHeadOfDepartment(@PathVariable("pbo") UUID pbo) {
        return ResponseEntity.ok(userService.getHeadOfDepartment(pbo));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DR_SPEC_ODELJENJA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @GetMapping("/subordinates")
    public ResponseEntity<UserListAndCountResponse> getSubordinates(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(userService.getSubordinates(PageRequest.of(page, size)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DR_SPEC_ODELJENJA') or hasRole('ROLE_VISA_MED_SESTRA')")
    @PostMapping("/add-shift/{lbz}")
    public ResponseEntity<UserShiftResponse> addShift(@PathVariable("lbz") UUID lbz,
                                                      @RequestBody AddShiftRequest request) {
        return ResponseEntity.ok(userService.addShift(lbz, request));
    }

    @GetMapping("/covid-nurses-num/{pbo}")
    public ResponseEntity<Integer> getNumOfCovidNursesByDepartment(@PathVariable("pbo") UUID pbo) {
        return ResponseEntity.ok(userService.getNumOfCovidNursesByDepartment(pbo));
    }
}
