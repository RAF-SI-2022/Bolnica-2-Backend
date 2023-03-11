package com.raf.si.userservice.service;

import com.raf.si.userservice.dto.request.LoginUserRequest;
import com.raf.si.userservice.dto.response.LoginUserResponse;

public interface AuthService {

    LoginUserResponse login(LoginUserRequest loginUserRequest);
}
