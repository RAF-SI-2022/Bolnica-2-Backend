package com.raf.si.userservice.service;

import com.raf.si.userservice.dto.request.*;
import com.raf.si.userservice.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest createUserRequest);

    UserResponse getUserByLbz(UUID lbz);

    boolean userExistsByLbzAndIsDeleted(UUID lbz);

    UserResponse deleteUser(Long id, UUID loggedLbz);

    UserResponse updateUser(UUID lbz, UpdateUserRequest updateUserRequest, boolean isAdmin);

    UserListAndCountResponse listUsers(String firstName, String lastName, String departmentName,
                                       String hospitalName, boolean includeDeleted, Boolean hasCovidAccess,
                                       Pageable pageable);

    MessageResponse resetPassword(PasswordResetRequest passwordResetRequest);

    MessageResponse updatePassword(UpdatePasswordRequest updatePasswordRequest);

    UserResponse updateCovidAccess(UUID lbz, boolean covidAccess);

    List<DoctorResponse> getAllDoctors();

    List<DoctorResponse> getAllDoctorsByDepartment(UUID pbo);

    List<UserResponse> getUsersByLbzList(UUIDListRequest lbzListRequest);

    DoctorResponse getHeadOfDepartment(UUID pbo);

    Integer getNumOfCovidNursesByDepartmentInTimeSlot(UUID pbo, TimeRequest request);

    UserListAndCountResponse getSubordinates(Pageable pageable);

    UserShiftResponse addShift(UUID lbz, AddShiftRequest request, String token);

    UserResponse updateDaysOff(UUID lbz, int daysOff);

    Boolean canScheduleForDoctor(UUID lbz, boolean covid, TimeRequest timeRequest);

    UserShiftResponse getUserWithShiftsByLbz(UUID lbz);
}
