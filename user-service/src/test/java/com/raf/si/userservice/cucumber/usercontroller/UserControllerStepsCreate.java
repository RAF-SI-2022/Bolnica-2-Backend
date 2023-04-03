package com.raf.si.userservice.cucumber.usercontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.userservice.cucumber.CucumberConfig;
import com.raf.si.userservice.cucumber.UtilsHelper;
import com.raf.si.userservice.dto.request.CreateUserRequest;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerStepsCreate extends CucumberConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private ResultActions resultActions;
    private Gson gson;
    private UtilsHelper util;

    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

    @When("admin provides valid information")
    public void admin_provides_valid_information() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        CreateUserRequest createUserRequest = util.createUserRequest();
        resultActions = mvc.perform(post("/users")
                .header("Authorization", "Bearer " + util.generateToken(admin))
                .content(gson.toJson(createUserRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("created user is returned")
    public void created_user_is_returned() throws Exception {
        User createdUser = userRepository.findUserByUsername("pera").orElse(null);
        assertNotNull(createdUser);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(createdUser.getEmail()))
                .andExpect(jsonPath("$.id").value(createdUser.getId()));

    }

    @When("admin tries to create a new employee with existing email")
    public void admin_tries_to_create_a_new_employee_with_existing_email() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        CreateUserRequest createUserRequest = util.createUserRequest();
        resultActions = mvc.perform(post("/users")
                .header("Authorization", "Bearer " + util.generateToken(admin))
                .content(gson.toJson(createUserRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("BadRequestException is thrown with status code {int}")
    public void bad_request_exception_is_thrown_with_status_code(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("given department does not exist")
    public void given_department_does_not_exist() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        CreateUserRequest createUserRequest = util.createUserRequest();
        createUserRequest.setEmail("user@gmail.com");
        createUserRequest.setDepartmentId(100L);
        resultActions = mvc.perform(post("/users")
                .header("Authorization", "Bearer " + util.generateToken(admin))
                .content(gson.toJson(createUserRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for given department")
    public void not_found_exception_is_thrown_with_status_code_for_given_department(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }
}
