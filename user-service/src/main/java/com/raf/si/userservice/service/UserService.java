package com.raf.si.userservice.service;

import com.raf.si.userservice.dto.response.UserResponse;
import com.raf.si.userservice.dto.request.CreateUserRequest;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest createUserRequest);
    UserResponse getUserByLbz(UUID lbz);
}
