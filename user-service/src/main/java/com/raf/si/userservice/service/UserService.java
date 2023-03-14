package com.raf.si.userservice.service;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.PasswordResetRequest;
import com.raf.si.userservice.dto.request.UpdatePasswordRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.dto.response.MessageResponse;
import com.raf.si.userservice.dto.response.UserListAndCountResponse;
import com.raf.si.userservice.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest createUserRequest);

    UserResponse getUserByLbz(UUID lbz);

    boolean userExistsByLbzAndIsDeleted(UUID lbz);

    UserResponse deleteUser(Long id);

    UserResponse updateUser(UUID lbz, UpdateUserRequest updateUserRequest, boolean isAdmin);

    UserListAndCountResponse listUsers(String firstName, String lastName, String departmentName,
                                       String hospitalName, boolean includeDeleted, Pageable pageable);

    MessageResponse resetPassword(PasswordResetRequest passwordResetRequest);

    MessageResponse updatePassword(UpdatePasswordRequest updatePasswordRequest);
}
