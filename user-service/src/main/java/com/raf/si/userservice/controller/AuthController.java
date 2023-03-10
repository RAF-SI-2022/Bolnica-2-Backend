package com.raf.si.userservice.controller;


import com.raf.si.userservice.dto.request.LoginUserRequest;
import com.raf.si.userservice.dto.response.LoginUserResponse;
import com.raf.si.userservice.exception.UnauthorizedException;
import com.raf.si.userservice.service.UserService;
import com.raf.si.userservice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
//    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//    @Autowired
//    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginUserRequest loginRequest) throws UnauthorizedException{
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
//        } catch (Exception e){
//            e.printStackTrace();
//            return ResponseEntity.status(401).build();
//        }
//
//        return ResponseEntity.ok(new LoginUserResponse(jwtUtil.generateToken(loginRequest.getEmail())));
//    }


}
