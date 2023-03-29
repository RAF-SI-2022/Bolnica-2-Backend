package com.raf.si.userservice.cucumber.usercontroller;

import com.raf.si.userservice.cucumber.CucumberConfig;
import com.raf.si.userservice.cucumber.UtilsHelper;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerStepsGet extends CucumberConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private ResultActions resultActions;
    private UtilsHelper utils;

    @Before
    public void initialization() {
        utils = new UtilsHelper(jwtUtil);
    }

    @When("given lbz is not user's lbz")
    public void given_lbz_is_not_user_s_lbz() throws Exception {
        User loggedUser = userRepository.findUserByUsername("medsestra").orElse(null);
        assertNotNull(loggedUser);
        resultActions = mvc.perform(get(String.format("/users/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + utils.generateEmployeeToken(loggedUser))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("ForbiddenException is thrown with status code {int} for given lbz")
    public void forbidden_exception_is_thrown_with_status_code_for_given_lbz(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("given user does not exist for given lbz")
    public void given_user_does_not_exist_for_given_lbz() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);

        resultActions = mvc.perform(get(String.format("/users/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for given lbz")
    public void not_found_exception_is_thrown_with_status_code_for_given_lbz(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("given user exists for given lbz")
    public void given_user_exists_for_given_lbz() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        User requestedUser = userRepository.findUserByUsername("medsestra").orElse(null);
        assertNotNull(admin);
        assertNotNull(requestedUser);
        resultActions = mvc.perform(get(String.format("/users/%s", requestedUser.getLbz()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("user is returned for given lbz")
    public void user_is_returned_for_given_lbz() throws Exception {
        User requestedUser = userRepository.findUserByUsername("medsestra").orElse(null);
        assertNotNull(requestedUser);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(requestedUser.getEmail()));
    }

    @When("given employee exists for given lbz")
    public void given_employee_exists_for_given_lbz() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        resultActions = mvc.perform(get(String.format("/users/employee-info/%s", admin.getLbz()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("employee is returned for given lbz")
    public void employee_is_returned_for_given_lbz() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(admin.getEmail()));
    }

    @When("request is sent for listing")
    public void request_is_sent_for_listing() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        Assert.assertNotNull(admin);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("firstName", "admin");

        resultActions = mvc.perform(get("/users").queryParams(queryParams)
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("page with given parameters is returned containing users")
    public void page_with_given_parameters_is_returned_containing_users() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userList", hasSize(1)));
    }
}
