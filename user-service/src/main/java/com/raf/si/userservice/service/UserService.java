package com.raf.si.userservice.service;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.PasswordResetRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.dto.response.CountResponse;
import com.raf.si.userservice.dto.response.MessageResponse;
import com.raf.si.userservice.dto.response.UserListResponse;
import com.raf.si.userservice.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest createUserRequest);

    UserResponse getUserByLbz(UUID lbz);

    boolean userExistsByLbzAndIsDeleted(UUID lbz);

    UserResponse deleteUser(Long id);

    UserResponse updateUser(UUID lbz, UpdateUserRequest updateUserRequest);

    List<UserListResponse> listUsers(String firstName, String lastName, String departmentName,
                                     String hospitalName, boolean includeDeleted, Pageable pageable);

    MessageResponse resetPassword(PasswordResetRequest passwordResetRequest);

    CountResponse getUsersCount();
}
