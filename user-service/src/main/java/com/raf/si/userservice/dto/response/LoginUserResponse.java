package com.raf.si.userservice.dto.response;

import lombok.Data;

@Data
public class LoginUserResponse {
    private String token;

    public LoginUserResponse(String token){
        this.token = token;
    }
}
