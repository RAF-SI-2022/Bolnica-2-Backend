package com.raf.si.userservice.unit;

import com.raf.si.userservice.dto.request.LoginUserRequest;
import com.raf.si.userservice.dto.response.LoginUserResponse;
import com.raf.si.userservice.exception.UnauthorizedException;
import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Hospital;
import com.raf.si.userservice.model.Permission;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.service.AuthService;
import com.raf.si.userservice.service.impl.AuthServiceImpl;
import com.raf.si.userservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    private AuthService authService;
    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        jwtUtil = new JwtUtil("secret key");
        passwordEncoder = mock(PasswordEncoder.class);
        authService = new AuthServiceImpl(userRepository, jwtUtil, passwordEncoder);
    }

    @Test
    public void login_WhenUsernameNotExist_ThrowsUnauthorizedException() {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("unknown");
        loginUserRequest.setPassword("unknown");

        when(userRepository.findUserByUsername(loginUserRequest.getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(loginUserRequest));
    }

    @Test
    public void login_WhenInvalidPassword_ThrowsUnauthorizedException() {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("username");
        loginUserRequest.setPassword("password");
        User user = createUser();

        when(userRepository.findUserByUsername(loginUserRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginUserRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(loginUserRequest));
    }

    @Test
    public void login_WhenUserIsDeleted_ThrowsUnauthorizedException() {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("username");
        loginUserRequest.setPassword("password");
        User user = createUser();
        user.setDeleted(true);

        when(userRepository.findUserByUsername(loginUserRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginUserRequest.getPassword(), user.getPassword())).thenReturn(true);

        assertThrows(UnauthorizedException.class, () -> authService.login(loginUserRequest));
    }

    @Test
    public void login_success() {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("username");
        loginUserRequest.setPassword("password");
        User user = createUser();
        user.setDeleted(false);

        when(userRepository.findUserByUsername(loginUserRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginUserRequest.getPassword(), user.getPassword())).thenReturn(true);

        LoginUserResponse loginUserResponse = new LoginUserResponse(jwtUtil.generateToken(setClaims(user), user.getLbz().toString()));

        assertThat(authService.login(loginUserRequest), instanceOf(LoginUserResponse.class));
    }

    private User createUser() {
        User user = new User();

        user.setUsername("username");
        user.setPassword("password");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setLbz(UUID.randomUUID());
        user.setPhone("231321231");
        user.setResidentialAddress("residentialAddress");
        user.setPlaceOfLiving("placeOfLiving");
        user.setDateOfBirth(new Date());
        user.setEmail("email");
        user.setTitle(Title.MR);
        user.setProfession(Profession.SPEC_ENDOKRINOLOG);

        Department department = new Department();
        department.setId(1L);
        department.setName("departmentName");
        department.setPbo(UUID.randomUUID());
        department.setDeleted(false);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setPbb(UUID.randomUUID());
        hospital.setFullName("hospitalFullName");
        hospital.setShortName("hospitalShortName");
        hospital.setAddress("address");
        hospital.setActivity("activity");
        hospital.setPlace("place");
        hospital.setDeleted(false);

        department.setHospital(hospital);
        user.setDepartment(department);

        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("ROLE_ADMIN");

        user.setPermissions(Collections.singletonList(permission));

        return user;
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
