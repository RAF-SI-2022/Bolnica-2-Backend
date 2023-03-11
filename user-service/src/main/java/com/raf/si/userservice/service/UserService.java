package com.raf.si.userservice.service;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest createUserRequest);

    UserResponse getUserByLbz(UUID lbz);

    boolean userExistsByLbzAndIsDeleted(UUID lbz);

    UserResponse deleteUser(Long id);

    UserResponse updateUser(UUID lbz, UpdateUserRequest updateUserRequest);
}
