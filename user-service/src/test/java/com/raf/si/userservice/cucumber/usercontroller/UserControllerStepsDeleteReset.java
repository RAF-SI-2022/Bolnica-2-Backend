package com.raf.si.userservice.cucumber.usercontroller;

import com.google.gson.Gson;
import com.raf.si.userservice.cucumber.CucumberConfig;
import com.raf.si.userservice.cucumber.UtilsHelper;
import com.raf.si.userservice.dto.request.PasswordResetRequest;
import com.raf.si.userservice.dto.request.UpdatePasswordRequest;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerStepsDeleteReset extends CucumberConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private ResultActions resultActions;
    private Gson gson = new Gson();
    private UtilsHelper utils;

    @Before
    public void initialization() {
        utils = new UtilsHelper(jwtUtil);
    }

    @When("user does not exist for given id")
    public void user_does_not_exist_for_given_id() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        resultActions = mvc.perform(delete(String.format("/users/%s", 100L))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for given id")
    public void not_found_exception_is_thrown_with_status_code_for_given_id(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("user exists for given id")
    public void user_exists_for_given_id() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        User user = userRepository.findUserByUsername("pera").orElse(null);
        assertNotNull(admin);
        assertNotNull(user);
        resultActions = mvc.perform(delete(String.format("/users/%s", user.getId()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("deleted user is returned")
    public void deleted_user_is_returned() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("pera"));
    }

    @When("given email does not exist")
    public void given_email_does_not_exist() throws Exception {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail("invalidemail@outlook.com");
        resultActions = mvc.perform(post("/users/reset-password")
                .content(gson.toJson(passwordResetRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for given email")
    public void not_found_exception_is_thrown_with_status_code_for_given_email(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("given email exists")
    public void given_email_exists() throws Exception {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setEmail("balkan.medic2023@outlook.com");
        resultActions = mvc.perform(post("/users/reset-password")
                .content(gson.toJson(passwordResetRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("Successful message is returned with email sent to user")
    public void successful_message_is_returned_with_email_sent_to_user() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @When("given password token is invalid")
    public void given_password_token_is_invalid() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setResetToken(UUID.randomUUID());
        updatePasswordRequest.setPassword("admin");

        resultActions = mvc.perform(post("/users/update-password")
                .content(gson.toJson(updatePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for given token")
    public void not_found_exception_is_thrown_with_status_code_for_given_token(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("given password token is valid")
    public void given_password_token_is_valid() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setResetToken(admin.getPasswordToken());
        updatePasswordRequest.setPassword("admin");

        resultActions = mvc.perform(post("/users/update-password")
                .content(gson.toJson(updatePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("Successful message is returned with status {int}")
    public void successful_message_is_returned_with_status(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.message").exists());
    }
}
