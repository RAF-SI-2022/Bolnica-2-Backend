package com.raf.si.userservice.service.impl;

import com.raf.si.userservice.dto.request.LoginUserRequest;
import com.raf.si.userservice.dto.response.LoginUserResponse;
import com.raf.si.userservice.exception.UnauthorizedException;
import com.raf.si.userservice.model.Permission;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.service.AuthService;
import com.raf.si.userservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginUserResponse login(LoginUserRequest loginUserRequest) {
        User user = userRepository.findUserByUsername(loginUserRequest.getUsername())
                .orElseThrow(() -> {
                    log.warn("Korisnik sa korisnickim imenom '{}' ne postoji", loginUserRequest.getUsername());
                    throw new UnauthorizedException("Korisnik sa datim kredencijalima ne postoji");
                });

        if (!passwordEncoder.matches(loginUserRequest.getPassword(), user.getPassword())) {
            log.warn("Korisnik je uneo pogresnu sifru za korisnicko ime '{}'", loginUserRequest.getUsername());
            throw new UnauthorizedException("Korisnik sa datim kredencijalima ne postoji");
        }

        if (user.isDeleted()) {
            log.warn("Korisnicki nalog sa email-om '{}' je deaktiviran", loginUserRequest.getUsername());
            throw new UnauthorizedException("Vas nalog je deaktiviran");
        }

        Claims claims = setClaims(user);
        return new LoginUserResponse(jwtUtil.generateToken(claims, user.getLbz().toString()));
    }

    private Claims setClaims(User user) {
        Claims claims = Jwts.claims();
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("title", user.getTitle().getNotation());
        claims.put("profession", user.getProfession().getNotation());
        claims.put("pbo", user.getDepartment().getPbo());
        claims.put("departmentName", user.getDepartment().getName());
        claims.put("pbb", user.getDepartment().getHospital().getPbb());
        claims.put("hospitalName", user.getDepartment().getHospital().getFullName());
        String[] roles = user.getPermissions().stream().map(Permission::getName).toArray(String[]::new);
        claims.put("permissions", roles);

        return claims;
    }
}
