package com.raf.si.userservice.controller;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.response.UserResponse;
import com.raf.si.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }
    @GetMapping("{lbz}")
    public ResponseEntity<?> getUserByLbz(@PathVariable("lbz") UUID lbz){
        return new ResponseEntity<>(userService.getUserByLbz(lbz), HttpStatus.OK);

    }
//    @PutMapping
//    Brisanje zaposlenog – reč je o soft delete-u, ne briše se zaposleni iz baze, već je
//    potrebno atribut “obrisan” setovatni na true. Ovo može uraditi samo
//    administrator. Kao odgovor se vraća informacija o uspešnosti akcije.

}
