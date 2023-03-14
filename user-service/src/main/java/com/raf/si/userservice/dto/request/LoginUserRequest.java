package com.raf.si.userservice.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginUserRequest {

    @NotEmpty(message = "Polje korisnicko ime ne sme biti prazno")
    private String username;
    @NotEmpty(message = "Polje sifra ne sme biti prazno")
    private String password;
}
