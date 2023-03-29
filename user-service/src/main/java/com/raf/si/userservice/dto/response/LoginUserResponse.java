package com.raf.si.userservice.dto.response;

import lombok.Getter;

@Getter
public class LoginUserResponse {

    private final String token;

    public LoginUserResponse(String token) {
        this.token = token;
    }
}
