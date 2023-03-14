package com.raf.si.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.dto.request.PasswordResetRequest;
import com.raf.si.userservice.dto.request.UpdatePasswordRequest;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(df);    }

    @Test
    public void createUser_Success() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        CreateUserRequest createUserRequest = createUserRequest();
        mvc.perform(post("/users")
                .header("Authorization", "Bearer " + generateToken(admin))
                .content(objectMapper.writeValueAsString(createUserRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(createUserRequest.getEmail()));
    }

    @Test
    public void getUserByLbz_Success() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        mvc.perform(get(String.format("/users/%s", admin.getLbz()))
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(admin.getEmail()));
    }

    @Test
    public void updateUser_Success() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        User user = userRepository.findUserByUsername("pera").orElse(null);
        assertNotNull(admin);
        assertNotNull(user);
        UpdateUserRequest updateUserRequest = createUpdateUserRequest();
        mvc.perform(put(String.format("/users/%s", user.getLbz()))
                .header("Authorization", "Bearer " + generateToken(admin))
                .content(objectMapper.writeValueAsString(updateUserRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(updateUserRequest.getEmail()));
    }

    @Test
    public void deleteUser_Success() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        User user = userRepository.findUserByUsername("pera").orElse(null);
        assertNotNull(admin);
        assertNotNull(user);
        mvc.perform(delete(String.format("/users/%s", user.getId()))
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    public void listUsers_Success() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("firstName", "admin");

        mvc.perform(get("/users").queryParams(queryParams)
                .header("Authorization", "Bearer " + generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userList", hasSize(1)));
    }

    @Test
    public void resetPassword_Success() throws Exception {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail("balkan.medic2023@outlook.com");
        mvc.perform(post("/users/reset-password")
                .content(objectMapper.writeValueAsString(passwordResetRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updatePassword_Success() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setResetToken(admin.getPasswordToken());
        updatePasswordRequest.setPassword("admin");

        mvc.perform(post("/users/update-password")
                .content(objectMapper.writeValueAsString(updatePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private String generateToken(User user) {
        Claims claims = Jwts.claims();
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("title", user.getTitle().getNotation());
        claims.put("profession", user.getProfession().getNotation());
        claims.put("pbo", user.getDepartment().getPbo());
        claims.put("departmentName", user.getDepartment().getName());
        claims.put("pbb", user.getDepartment().getHospital().getPbb());
        claims.put("hospitalName", user.getDepartment().getHospital().getFullName());
        String[] roles = new String[]{"ROLE_ADMIN", "ROLE_DR_SPEC_ODELJENJA", "ROLE_DR_SPEC",
                "ROLE_DR_SPEC_POV", "ROLE_VISA_MED_SESTRA", "ROLE_MED_SESTRA"};
        claims.put("permissions", roles);
        return jwtUtil.generateToken(claims, user.getLbz().toString());
    }

    private CreateUserRequest createUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setFirstName("Pera");
        createUserRequest.setLastName("Peric");
        createUserRequest.setDateOfBirth(new Date());
        createUserRequest.setGender("muski");
        createUserRequest.setJmbg("5312412312");
        createUserRequest.setResidentialAddress("Perina adresa");
        createUserRequest.setPlaceOfLiving("Srbija");
        createUserRequest.setPhone("0351341221");
        createUserRequest.setEmail("pera@gmail.com");
        createUserRequest.setTitle(Title.MR.getNotation());
        createUserRequest.setProfession(Profession.SPEC_HIRURG.getNotation());
        createUserRequest.setDepartmentId(1L);
        createUserRequest.setPermissions(new String[]{"ROLE_DR_SPEC"});

        return createUserRequest;
    }

    private UpdateUserRequest createUpdateUserRequest() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setDepartmentId(1L);
        updateUserRequest.setJmbg("5312412312");
        updateUserRequest.setEmail("pera123@gmail.com");
        updateUserRequest.setGender("muski");
        updateUserRequest.setFirstName("Pera");
        updateUserRequest.setLastName("Peric");
        updateUserRequest.setPhone("0123123112");
        updateUserRequest.setUsername("pera");
        updateUserRequest.setDateOfBirth(new Date());
        updateUserRequest.setProfession(Profession.SPEC_GASTROENTEROLOG.getNotation());
        updateUserRequest.setTitle(Title.MR.getNotation());
        updateUserRequest.setPlaceOfLiving("Srbija");
        updateUserRequest.setResidentialAddress("Adresa");

        return updateUserRequest;
    }
}
