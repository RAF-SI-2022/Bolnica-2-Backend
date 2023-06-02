package com.raf.si.userservice.mapper;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.dto.response.DoctorResponse;
import com.raf.si.userservice.dto.response.UserListAndCountResponse;
import com.raf.si.userservice.dto.response.UserListResponse;
import com.raf.si.userservice.dto.response.UserResponse;
import com.raf.si.userservice.exception.BadRequestException;
import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Permission;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User requestToModel(CreateUserRequest createUserRequest, Department department,
                               List<Permission> permissions) {


        User user = new User();
        user.setEmail(createUserRequest.getEmail());
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        user.setGender(createUserRequest.getGender());
        user.setJMBG(createUserRequest.getJmbg());
        user.setUsername(getExtractedPrefix(createUserRequest.getEmail()));
        user.setPassword(passwordEncoder.encode(getExtractedPrefix(createUserRequest.getEmail())));
        user.setDepartment(department);
        user.setDateOfBirth(createUserRequest.getDateOfBirth());
        user.setPlaceOfLiving(createUserRequest.getPlaceOfLiving());
        user.setResidentialAddress(createUserRequest.getResidentialAddress());
        user.setPermissions(permissions);

        Profession profession = Profession.valueOfNotation(createUserRequest.getProfession());

        Title title = Title.valueOfNotation(createUserRequest.getTitle());
        if (profession == null) {
            log.error("Nepoznata profesija '{}'", createUserRequest.getProfession());
            throw new BadRequestException("Nepoznata profesija");
        }

        if (title == null) {
            log.error("Nepoznata titula '{}'", createUserRequest.getTitle());
            throw new BadRequestException("Nepoznata titula");
        }

        user.setProfession(profession);
        user.setTitle(title);

        if (createUserRequest.getPhone() != null)
            user.setPhone(createUserRequest.getPhone());

        return user;
    }

    public UserResponse modelToResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setUsername(user.getUsername());
        userResponse.setGender(user.getGender());
        userResponse.setDateOfBirth(user.getDateOfBirth());
        userResponse.setLbz(user.getLbz());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setDeleted(user.isDeleted());
        userResponse.setJMBG(user.getJMBG());
        userResponse.setProfession(user.getProfession());
        userResponse.setTitle(user.getTitle());
        userResponse.setResidentalAddress(user.getResidentialAddress());
        userResponse.setPlaceOfLiving(user.getPlaceOfLiving());
        userResponse.setDepartment(user.getDepartment());
        userResponse.setPermissions(user.getPermissions().stream().map(Permission::getName).collect(Collectors.toList()));
        userResponse.setCovidAccess(user.isCovidAccess());

        return userResponse;
    }

    public User updateRequestToModel(User user, UpdateUserRequest updateUserRequest,
                                     Department department) {


        user.setEmail(updateUserRequest.getEmail());
        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        user.setGender(updateUserRequest.getGender());
        user.setJMBG(updateUserRequest.getJmbg());
        user.setUsername(updateUserRequest.getUsername());
        user.setDepartment(department);
        user.setDateOfBirth(updateUserRequest.getDateOfBirth());
        user.setPlaceOfLiving(updateUserRequest.getPlaceOfLiving());
        user.setResidentialAddress(updateUserRequest.getResidentialAddress());

        Profession profession = Profession.valueOfNotation(updateUserRequest.getProfession());
        Title title = Title.valueOfNotation(updateUserRequest.getTitle());

        if (profession == null) {
            log.error("Nepoznata profesija '{}'", updateUserRequest.getProfession());
            throw new BadRequestException("Nepoznata profesija");
        }

        if (title == null) {
            log.error("Nepoznata titula '{}'", updateUserRequest.getTitle());
            throw new BadRequestException("Nepoznata titula");
        }

        user.setProfession(profession);
        user.setTitle(title);

        if (updateUserRequest.getPhone() != null)
            user.setPhone(updateUserRequest.getPhone());

        if (updateUserRequest.getNewPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUserRequest.getNewPassword()));
        }

        return user;
    }

    public User updateRegularRequestToModel(User user, UpdateUserRequest updateUserRequest) {

        if (updateUserRequest.getPhone() != null)
            user.setPhone(updateUserRequest.getPhone());

        if (updateUserRequest.getOldPassword() != null && updateUserRequest.getNewPassword() != null) {
            if (!passwordEncoder.matches(updateUserRequest.getOldPassword(), user.getPassword())) {
                log.error("Pogresno uneta sifra za korisnika sa id-ijem '{}'", user.getId());
                throw new BadRequestException("Pogresno uneta sifra");
            }
            user.setPassword(passwordEncoder.encode(updateUserRequest.getNewPassword()));
        }

        return user;
    }

    public UserListAndCountResponse modelToUserListAndCountResponse(Page<User> userPage) {
        List<UserListResponse> userListResponseList = userPage.stream()
                .map(this::userListResponseToModel)
                .collect(Collectors.toList());

        return new UserListAndCountResponse(userListResponseList, userPage.getTotalElements());
    }

    public User setUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordToken(UUID.randomUUID());
        return user;
    }

    public DoctorResponse modelToDoctorResponse(User user) {
        DoctorResponse doctorResponse = new DoctorResponse();

        doctorResponse.setLbz(user.getLbz());
        doctorResponse.setFirstName(user.getFirstName());
        doctorResponse.setLastName(user.getLastName());
        doctorResponse.setCovidAccess(user.isCovidAccess());

        return doctorResponse;
    }

    private UserListResponse userListResponseToModel(User user) {
        UserListResponse userListResponse = new UserListResponse();
        userListResponse.setId(user.getId());
        userListResponse.setLbz(user.getLbz());
        userListResponse.setEmail(user.getEmail());
        userListResponse.setFirstName(user.getFirstName());
        userListResponse.setLastName(user.getLastName());
        userListResponse.setTitle(user.getTitle());
        userListResponse.setProfession(user.getProfession());
        userListResponse.setPhone(user.getPhone());
        userListResponse.setDateOfBirth(user.getDateOfBirth());
        userListResponse.setDepartmentName(user.getDepartment().getName());
        userListResponse.setHospitalName(user.getDepartment().getHospital().getFullName());
        userListResponse.setCovidAccess(user.isCovidAccess());

        return userListResponse;
    }

    private String getExtractedPrefix(String fullString) {
        return fullString.substring(0, fullString.indexOf('@'));
    }


}
