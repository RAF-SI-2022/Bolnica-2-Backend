package com.raf.si.userservice.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LoginUserResponse {

    private String token;

    public LoginUserResponse(String token) {
        this.token = token;
    }
}
