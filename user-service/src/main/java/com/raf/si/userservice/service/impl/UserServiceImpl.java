package com.raf.si.userservice.service.impl;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.PasswordResetRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.dto.response.MessageResponse;
import com.raf.si.userservice.dto.response.UserListAndCountResponse;
import com.raf.si.userservice.dto.response.UserResponse;
import com.raf.si.userservice.exception.BadRequestException;
import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.mapper.UserMapper;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.service.EmailService;
import com.raf.si.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        userRepository.findUserByEmail(createUserRequest.getEmail()).ifPresent((k) -> {
            log.error("Korisnik sa mejlom '{}' vec postoji", createUserRequest.getEmail());
            throw new BadRequestException("Korisnik sa datim email-om vec postoji");
        });

        User user = userRepository.save(userMapper.requestToModel(createUserRequest));
        log.info("Korisnik sa id-ijem '{}' uspesno sacuvan", user.getId());

        return userMapper.modelToResponse(user);
    }

    @Override
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
    public UserResponse deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("Ne postoji korisnik sa id-ijem '{}'", id);
            throw new NotFoundException("Korisnik sa datim id-ijem ne postoji");
        });
        user.setDeleted(!user.isDeleted());
        user = userRepository.save(user);
        log.info("Korisnicki nalog sa id-ijem '{}' je uspesno {}", id, user.isDeleted() ? "deaktiviran" : "aktiviran");

        return userMapper.modelToResponse(user);
    }

    @Transactional
    @Override
    public UserResponse updateUser(UUID lbz, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findUserByLbz(lbz).orElseThrow(() -> {
            log.error("Ne postoji korisnik sa lbz '{}'", lbz);
            throw new NotFoundException("Korisnik sa datim lbz-om ne postoji");
        });

        User updatedUser = userMapper.updateRequestToModel(user, updateUserRequest);

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
        UUID password = UUID.randomUUID();

        emailService.resetPassword(user.getEmail(), password);

        user = userRepository.save(userMapper.setUserPassword(user, password.toString()));

        log.info("Sifra uspesno sacuvana za email '{}'", user.getEmail());

        return new MessageResponse("Email sa novom sifrom je poslat na vasu email adresu");
    }

    private List<Boolean> adjustIncludeDeleteParameter(boolean includeDeleted) {
        List<Boolean> list = new ArrayList<>();
        list.add(false);
        if (includeDeleted)
            list.add(true);
        return list;
    }
}
