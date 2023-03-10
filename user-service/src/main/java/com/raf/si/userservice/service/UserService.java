package com.raf.si.userservice.service;

import com.raf.si.userservice.dto.response.UserResponse;
import com.raf.si.userservice.dto.request.CreateUserRequest;

public interface UserService {

    UserResponse createUser(CreateUserRequest createUserRequest);
}
