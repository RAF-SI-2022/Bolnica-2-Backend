package com.raf.si.userservice.controller;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.PasswordResetRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.dto.response.CountResponse;
import com.raf.si.userservice.dto.response.MessageResponse;
import com.raf.si.userservice.dto.response.UserListResponse;
import com.raf.si.userservice.dto.response.UserResponse;
import com.raf.si.userservice.exception.ForbiddenException;
import com.raf.si.userservice.service.EmailService;
import com.raf.si.userservice.service.UserService;
import com.raf.si.userservice.utils.TokenPayload;
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
    private final EmailService emailService;


    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }

    @GetMapping("/{lbz}")
    public ResponseEntity<UserResponse> getUserByLbz(@PathVariable("lbz") UUID lbz) {
        TokenPayload payload = getPayload();

        if (!payload.getPermissions().contains("ROLE_ADMIN") && !lbz.equals(payload.getLbz())) {
            log.error("LBZ nije isti za korisnika sa lbz-om '{}' i trazeni lbz '{}'", lbz, payload.getLbz());
            throw new ForbiddenException("Nemate permisije za ovu akciju");
        }

        return ResponseEntity.ok(userService.getUserByLbz(lbz));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @PutMapping("/{lbz}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("lbz") UUID lbz,
                                                   @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        TokenPayload payload = getPayload();

        if (!payload.getPermissions().contains("ROLE_ADMIN") && !lbz.equals(payload.getLbz())) {
            log.error("LBZ nije isti za korisnika sa lbz-om '{}' i trazeni lbz '{}'", lbz, payload.getLbz());
            throw new ForbiddenException("Nemate permisije za ovu akciju");
        }

        return ResponseEntity.ok(userService.updateUser(lbz, updateUserRequest));
    }

    @GetMapping
    public ResponseEntity<List<UserListResponse>> listUsers(@RequestParam(defaultValue = "") String firstName,
                                                            @RequestParam(defaultValue = "") String lastName,
                                                            @RequestParam(defaultValue = "") String departmentName,
                                                            @RequestParam(defaultValue = "") String hospitalName,
                                                            @RequestParam(defaultValue = "false") boolean includeDeleted,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(userService.listUsers(firstName, lastName, departmentName, hospitalName, includeDeleted, PageRequest.of(page, size)));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        return ResponseEntity.ok(userService.resetPassword(passwordResetRequest));
    }

    @GetMapping("/count")
    public ResponseEntity<CountResponse> getUserCount() {
        return ResponseEntity.ok(userService.getUsersCount());
    }

    private TokenPayload getPayload() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        return (TokenPayload) authentication.getPrincipal();
    }

}
