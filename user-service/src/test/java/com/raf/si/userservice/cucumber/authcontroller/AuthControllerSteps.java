package com.raf.si.userservice.cucumber.authcontroller;

import com.google.gson.Gson;
import com.raf.si.userservice.cucumber.CucumberConfig;
import com.raf.si.userservice.dto.request.LoginUserRequest;
import com.raf.si.userservice.dto.response.LoginUserResponse;
import com.raf.si.userservice.exception.ErrorCode;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.utils.JwtUtil;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerSteps extends CucumberConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private ResultActions resultActions;
    private final Gson gson = new Gson();

    @When("user tries to log in with provided invalid username")
    public void user_tries_to_log_in_with_provided_invalid_username() throws Exception {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("user");
        loginUserRequest.setPassword("password");

        resultActions = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(loginUserRequest)));
    }
    @Then("Unauthorized error code and status {int} is returned for given invalid username")
    public void unauthorized_error_code_and_status_is_returned_for_given_invalid_username(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.UNAUTHORIZED.toString()));
    }

    @When("user tries to log in with provided valid username and invalid password")
    public void user_tries_to_log_in_with_provided_valid_username_and_invalid_password() throws Exception {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("admin");
        loginUserRequest.setPassword("wrong password");

        resultActions = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(loginUserRequest)));
    }
    @Then("Unauthorized error code and status {int} is returned for given invalid password")
    public void unauthorized_error_code_and_status_is_returned_for_given_invalid_password(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.UNAUTHORIZED.toString()));
    }

    @When("user tries to log in on account which is deleted")
    public void user_tries_to_log_in_on_account_which_is_deleted() throws Exception {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("admin");
        loginUserRequest.setPassword("admin");

        setUserDelete(true);

        resultActions = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(loginUserRequest)));
    }
    @Then("Unauthorized error code and status {int} is returned for deleted account")
    public void unauthorized_error_code_and_status_is_returned_for_deleted_account(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.UNAUTHORIZED.toString()));
        setUserDelete(false);
    }

    @When("user tries to log in with provided valid credentials")
    public void user_tries_to_log_in_with_provided_valid_credentials() throws Exception {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("admin");
        loginUserRequest.setPassword("admin");

        resultActions = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(loginUserRequest)));
    }

    @Then("valid token is returned")
    public void valid_token_is_returned() throws Exception {
        User user = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(user);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        String content = resultActions.andReturn().getResponse().getContentAsString();
        LoginUserResponse response = gson.fromJson(content, LoginUserResponse.class);
        Claims claims = jwtUtil.extractAllClaims(response.getToken());

        assertEquals(claims.getSubject(), user.getLbz().toString());
    }

    private void setUserDelete(boolean isDeleted) {
        User user = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(user);
        user.setDeleted(isDeleted);
        userRepository.save(user);
    }
}
