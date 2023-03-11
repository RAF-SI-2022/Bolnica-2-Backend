package com.raf.si.userservice.controller;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.dto.response.UserResponse;
import com.raf.si.userservice.exception.ForbiddenException;
import com.raf.si.userservice.service.UserService;
import com.raf.si.userservice.utils.TokenPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    private TokenPayload getPayload() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        return (TokenPayload) authentication.getPrincipal();
    }

}
