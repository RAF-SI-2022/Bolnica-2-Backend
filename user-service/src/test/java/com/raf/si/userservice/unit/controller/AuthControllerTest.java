package com.raf.si.userservice.unit.controller;


import com.raf.si.userservice.controller.AuthController;
import com.raf.si.userservice.dto.request.LoginUserRequest;
import com.raf.si.userservice.dto.response.LoginUserResponse;
import com.raf.si.userservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    private AuthController authController;
    private AuthService authService;

    @BeforeEach
    public void setUp() {
       authService = mock(AuthService.class);
       authController = new AuthController(authService);
    }

    @Test
    public void login_success() {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("username");
        loginUserRequest.setPassword("password");

        LoginUserResponse loginUserResponse = new LoginUserResponse("token");

        when(authService.login(loginUserRequest)).thenReturn(loginUserResponse);

        assertEquals(authController.login(loginUserRequest),
                ResponseEntity.of(Optional.of(loginUserResponse)));
    }

}
