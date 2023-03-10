package com.raf.si.userservice.service.impl;

import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.response.UserResponse;
import com.raf.si.userservice.exception.BadRequestException;
import com.raf.si.userservice.exception.NotFoundException;
import com.raf.si.userservice.exception.UnauthorizedException;
import com.raf.si.userservice.mapper.UserMapper;
import com.raf.si.userservice.model.Permission;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        userRepository.findUserByEmail(createUserRequest.getEmail()).ifPresent((k) -> {
            log.error("Korisnik sa mejlom '{}' ne postoji", createUserRequest.getEmail());
            throw new BadRequestException("Korisnik sa datim email-om vec postoji");
        });

        User user = userRepository.save(userMapper.requestToModel(createUserRequest));
        log.info("Korisnik sa id-ijem '{}' uspesno sacuvan", user.getId());

        return userMapper.modelToResponse(user);
    }

    @Override
    public UserResponse getUserByLbz(UUID lbz) {
        User user = userRepository.findUserByLbz(lbz).orElseThrow(() -> new NotFoundException(String.format("Ne postoji korisnik sa lbz-om: %s ", lbz)));
        return userMapper.modelToResponse(user);

    }
    private void doesUserHavePermissionTo(String email,String privilege, String errorMessage) {
        User loggedUser = userRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(String.format("No user with email: %s found.", email)));
        int flag = 0;
        for (Permission permission : loggedUser.getPermissions()) if (permission.getName().equals(privilege)) flag = 1;
        if (flag == 0) throw new UnauthorizedException("Nemate privilegiju da " + errorMessage + ".");
    }


}
