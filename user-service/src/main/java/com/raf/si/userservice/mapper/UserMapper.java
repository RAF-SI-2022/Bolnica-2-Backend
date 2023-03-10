package com.raf.si.userservice.mapper;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.response.UserResponse;
import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.repository.DepartmentRepository;
import com.raf.si.userservice.repository.PermissionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final PermissionsRepository permissionsRepository;

    public UserMapper(PasswordEncoder passwordEncoder, DepartmentRepository departmentRepository,
                      PermissionsRepository permissionsRepository) {
        this.passwordEncoder = passwordEncoder;
        this.departmentRepository = departmentRepository;
        this.permissionsRepository = permissionsRepository;
    }

    public User requestToModel(CreateUserRequest createUserRequest) {
        Department department = departmentRepository.findById(
                createUserRequest.getDepartmentId()).orElseThrow(() -> {
                    log.error("Odeljenje sa id-ijem '{}' ne postoji", createUserRequest.getDepartmentId());
                    throw new NotFoundException("Odeljenje sa datim id-ijem ne postoji");
                }
        );

        User user = new User();
        user.setLbz(createUserRequest.getLbz());
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
        user.setPermissions(permissionsRepository.findPermissionsByNameIsIn(Arrays.asList(createUserRequest.getPermissions())));
        user.setProfession(Profession.valueOfNotation(createUserRequest.getProfession()));
        user.setTitle(Title.valueOfNotation(createUserRequest.getTitle()));

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
        userResponse.setPermissions(user.getPermissions());

        return userResponse;
    }

    private String getExtractedPrefix(String email) {
        String EMAIL_SUFFIX = "@ibis.rs";
        return email.replace(EMAIL_SUFFIX, "");
    }


}
