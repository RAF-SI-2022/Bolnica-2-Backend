package com.raf.si.userservice.controller;


import com.raf.si.userservice.dto.request.LoginUserRequest;
import com.raf.si.userservice.dto.response.LoginUserResponse;
import com.raf.si.userservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@Valid @RequestBody LoginUserRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }


}
