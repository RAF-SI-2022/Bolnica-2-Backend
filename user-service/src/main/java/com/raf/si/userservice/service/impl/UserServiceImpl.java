package com.raf.si.userservice.service.impl;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.PasswordResetRequest;
import com.raf.si.userservice.dto.request.UpdatePasswordRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.dto.response.*;
import com.raf.si.userservice.exception.BadRequestException;
import com.raf.si.userservice.exception.ForbiddenException;
import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.mapper.UserMapper;
import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Permission;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.repository.DepartmentRepository;
import com.raf.si.userservice.repository.PermissionsRepository;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.service.EmailService;
import com.raf.si.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final DepartmentRepository departmentRepository;
    private final PermissionsRepository permissionsRepository;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           EmailService emailService, DepartmentRepository departmentRepository,
                           PermissionsRepository permissionsRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.departmentRepository = departmentRepository;
        this.permissionsRepository = permissionsRepository;
    }

    @Transactional
    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        userRepository.findUserByEmail(createUserRequest.getEmail()).ifPresent((k) -> {
            log.error("Korisnik sa mejlom '{}' vec postoji", createUserRequest.getEmail());
            throw new BadRequestException("Korisnik sa datim email-om vec postoji");
        });

        Department department = departmentRepository.findById(createUserRequest.getDepartmentId())
                .orElseThrow(() -> {
                            log.error("Odeljenje sa id-ijem '{}' ne postoji", createUserRequest.getDepartmentId());
                            throw new NotFoundException("Odeljenje sa datim id-ijem ne postoji");
                        }
                );

        List<Permission> permissions = permissionsRepository.findPermissionsByNameIsIn(
                Arrays.asList(createUserRequest.getPermissions())
        );


        User user = userRepository.save(userMapper.requestToModel(createUserRequest, department, permissions));
        log.info("Korisnik sa id-ijem '{}' uspesno sacuvan", user.getId());

        return userMapper.modelToResponse(user);
    }

    @Override
    @Cacheable(value = "user", key = "#lbz")
    public UserResponse getUserByLbz(UUID lbz) {
        User user = userRepository.findUserByLbz(lbz).orElseThrow(() -> {
            log.error("Ne postoji korisnik sa lbz-om '{}'", lbz);
            throw new NotFoundException(String.format("Ne postoji korisnik sa lbz-om: %s ", lbz));
        });
        return userMapper.modelToResponse(user);

    }

    @Override
    public boolean userExistsByLbzAndIsDeleted(UUID lbz) {
        return userRepository.userExists(lbz, false);
    }

    @Transactional
    @Override
    @CacheEvict(value = "user", key = "#loggedLbz")
    public UserResponse deleteUser(Long id, UUID loggedLbz) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("Ne postoji korisnik sa id-ijem '{}'", id);
            throw new NotFoundException("Korisnik sa datim id-ijem ne postoji");
        });

        if (user.getLbz().equals(loggedLbz)) {
            log.error("Korisnik je pokusao obrisati sam sebe, lbz '{}'", loggedLbz);
            throw new ForbiddenException("Ova akcija nije dozvoljena");
        }

        user.setDeleted(true);
        user = userRepository.save(user);
        log.info("Korisnicki nalog sa id-ijem '{}' je uspesno obrisan", id);

        return userMapper.modelToResponse(user);
    }

    @Transactional
    @Override
    @CacheEvict(value = "user", key = "#loggedLbz")
    public UserResponse updateUser(UUID lbz, UpdateUserRequest updateUserRequest, boolean isAdmin) {
        User user = userRepository.findUserByLbz(lbz).orElseThrow(() -> {
            log.error("Ne postoji korisnik sa lbz '{}'", lbz);
            throw new NotFoundException("Korisnik sa datim lbz-om ne postoji");
        });

        Department department = departmentRepository.findById(updateUserRequest.getDepartmentId())
                .orElseThrow(() -> {
                            log.error("Odeljenje sa id-ijem '{}' ne postoji", updateUserRequest.getDepartmentId());
                            throw new NotFoundException("Odeljenje sa datim id-ijem ne postoji");
                        }
                );

        User updatedUser = isAdmin ? userMapper.updateRequestToModel(user, updateUserRequest, department)
                : userMapper.updateRegularRequestToModel(user, updateUserRequest);

        updatedUser = userRepository.save(updatedUser);
        log.info("Korisnik sa lbz-om '{}' uspesno update-ovan", lbz);
        return userMapper.modelToResponse(updatedUser);
    }

    @Override
    public UserListAndCountResponse listUsers(String firstName, String lastName,
                                              String departmentName, String hospitalName,
                                              boolean includeDeleted, Pageable pageable) {

        return userMapper.modelToUserListAndCountResponse(userRepository.listAllUsers(firstName.toLowerCase(), lastName.toLowerCase(),
                departmentName.toLowerCase(), hospitalName.toLowerCase(),
                adjustIncludeDeleteParameter(includeDeleted), pageable));
    }

    @Override
    public MessageResponse resetPassword(PasswordResetRequest passwordResetRequest) {
        User user = userRepository.findUserByEmail(passwordResetRequest.getEmail()).orElseThrow(() -> {
            log.error("Korisnik sa email-om '{}' ne postoji", passwordResetRequest.getEmail());
            throw new NotFoundException("Korisnik sa datim email-om ne postoji");
        });

        emailService.resetPassword(user.getEmail(), user.getPasswordToken());

        return new MessageResponse("Proverite vas email za resetovanje sifre");
    }

    @CachePut(value = "user", key = "#user.lbz")
    private User updateUserPassword(User user, UpdatePasswordRequest updatePasswordRequest) {
        return userRepository.save(userMapper.setUserPassword(user, updatePasswordRequest.getPassword()));
    }

    @Override
    public MessageResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User user = userRepository.findByPasswordToken(updatePasswordRequest.getResetToken())
                .orElseThrow(() -> {
                    log.error("Token sifra '{}' ne postoji", updatePasswordRequest.getResetToken());
                    throw new NotFoundException("Token sifra ne postoji");
                });
        User updatedUser = updateUserPassword(user, updatePasswordRequest);
        log.info("Sifra promenjena za korisnika sa email-om '{}'", updatedUser.getEmail());

        return new MessageResponse("Sifra je uspesno promenjena");
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        List<String> doctorPermissions = Arrays.asList("ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC", "ROLE_DR_SPEC_POV");
        log.info("Listanje svih doktora...");
        return userRepository.getAllDoctors(doctorPermissions)
                .stream()
                .map(userMapper::modelToDoctorResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorResponse> getAllDoctorsByDepartment(UUID pbo) {
        List<String> doctorPermissions = Arrays.asList("ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC", "ROLE_DR_SPEC_POV");
        Department department = departmentRepository.findDepartmentByPbo(pbo).orElseThrow(() -> {
            log.error("Odeljenje sa pbo '{}' ne postoji", pbo);
            throw new NotFoundException("Odeljenje sa datim pbo ne postoji");
        });

        log.info("Listanje svih doktora po odeljenju...");
        return userRepository.getAllDoctorsByDepartment(doctorPermissions, department)
                .stream()
                .map(userMapper::modelToDoctorResponse)
                .collect(Collectors.toList());

    }

    private List<Boolean> adjustIncludeDeleteParameter(boolean includeDeleted) {
        List<Boolean> list = new ArrayList<>();
        list.add(false);
        if (includeDeleted)
            list.add(true);
        return list;
    }
}
