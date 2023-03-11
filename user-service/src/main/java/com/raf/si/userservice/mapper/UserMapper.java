package com.raf.si.userservice.mapper;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.dto.response.UserResponse;
import com.raf.si.userservice.exception.BadRequestException;
import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Permission;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.repository.DepartmentRepository;
import com.raf.si.userservice.repository.PermissionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

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

        return userResponse;
    }

    public User updateRequestToModel(User user, UpdateUserRequest updateUserRequest) {
        Department department = departmentRepository.findById(updateUserRequest.getDepartmentId()).orElseThrow(() -> {
                    log.error("Odeljenje sa id-ijem '{}' ne postoji", updateUserRequest.getDepartmentId());
                    throw new NotFoundException("Odeljenje sa datim id-ijem ne postoji");
                }
        );

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

        if (updateUserRequest.getOldPassword() != null && updateUserRequest.getNewPassword() != null) {
            if (!passwordEncoder.matches(updateUserRequest.getOldPassword(), user.getPassword())) {
                log.error("Pogresno uneta sifra za korisnika sa id-ijem '{}'", user.getId());
                throw new BadRequestException("Pogresno uneta sifra");
            }
            user.setPassword(passwordEncoder.encode(updateUserRequest.getNewPassword()));
        }

        return user;
    }



    private String getExtractedPrefix(String fullString) {
        return fullString.substring(0, fullString.indexOf('@'));
    }


}
