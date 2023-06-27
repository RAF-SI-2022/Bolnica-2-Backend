package com.raf.si.userservice.cucumber.usercontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.userservice.cucumber.CucumberConfig;
import com.raf.si.userservice.cucumber.UtilsHelper;
import com.raf.si.userservice.dto.request.UpdateUserRequest;
import com.raf.si.userservice.model.Shift;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.ShiftType;
import com.raf.si.userservice.repository.ShiftRepository;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerStepsUpdate extends CucumberConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    ShiftRepository shiftRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private ResultActions resultActions;
    private Gson gson;
    private UtilsHelper utils;

    @Before
    public void initialization() {
        utils = new UtilsHelper(jwtUtil);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

    @When("given lbz is not matched with logged user lbz")
    public void given_lbz_is_not_matched_with_logged_user_lbz() throws Exception {
        User user = userRepository.findUserByUsername("pera").orElse(null);
        assertNotNull(user);
        UpdateUserRequest updateUserRequest = utils.createUpdateUserRequest();
        resultActions = mvc.perform(put(String.format("/users/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + utils.generateEmployeeToken(user))
                .content(gson.toJson(updateUserRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("ForbiddenException is thrown with status code {int} for invalid lbz")
    public void forbidden_exception_is_thrown_with_status_code_for_invalid_lbz(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("given lbz is matched with logged user lbz")
    public void given_lbz_is_matched_with_logged_user_lbz() throws Exception {
        User user = userRepository.findUserByUsername("pera").orElse(null);
        assertNotNull(user);
        UpdateUserRequest updateUserRequest = utils.createUpdateUserRequest();
        resultActions = mvc.perform(put(String.format("/users/%s", user.getLbz()))
                .header("Authorization", "Bearer " + utils.generateEmployeeToken(user))
                .content(gson.toJson(updateUserRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("updated user is returned")
    public void updated_user_is_returned() throws Exception {
        UpdateUserRequest updateUserRequest = utils.createUpdateUserRequest();
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value(updateUserRequest.getPhone()));
    }

    @When("given lbz does not exist in database")
    public void given_lbz_does_not_exist_in_database() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        UpdateUserRequest updateUserRequest = utils.createUpdateUserRequest();
        resultActions = mvc.perform(put(String.format("/users/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .content(gson.toJson(updateUserRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for invalid lbz")
    public void not_found_exception_is_thrown_with_status_code_for_invalid_lbz(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("department id does not exist in database")
    public void department_id_does_not_exist_in_database() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        User user = userRepository.findUserByUsername("pera").orElse(null);
        assertNotNull(admin);
        assertNotNull(user);
        UpdateUserRequest updateUserRequest = utils.createUpdateUserRequest();
        updateUserRequest.setDepartmentId(100L);
        resultActions = mvc.perform(put(String.format("/users/%s", user.getLbz()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .content(gson.toJson(updateUserRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for invalid department")
    public void not_found_exception_is_thrown_with_status_code_for_invalid_department(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("request is valid")
    public void request_is_valid() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        User user = userRepository.findUserByUsername("pera").orElse(null);
        assertNotNull(admin);
        assertNotNull(user);
        UpdateUserRequest updateUserRequest = utils.createUpdateUserRequest();
        resultActions = mvc.perform(put(String.format("/users/%s", user.getLbz()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .content(gson.toJson(updateUserRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("updated user is returned with status {int}")
    public void updated_user_is_returned_with_status(Integer statusCode) throws Exception {
        UpdateUserRequest updateUserRequest = utils.createUpdateUserRequest();
        resultActions.andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.firstName").value(updateUserRequest.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updateUserRequest.getLastName()));
    }

    @When("User tries to update covid access for someone")
    public void user_tries_to_update_covid_access_for_someone() throws Exception {
        User admin = userRepository.findUserByUsername("admin").get();
        Assertions.assertNotNull(admin);

        resultActions = mvc.perform(put(String.format("/users/update-covid-access/%s?covidAccess=true", admin.getLbz()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("User's covid access is updated in the database")
    public void user_s_covid_access_is_updated_in_the_database() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.covidAccess").value(true));
    }

    @Given("Someone who used all days off for next year")
    public void someone_who_used_all_days_off_for_next_year() {
        User admin = userRepository.findUserByUsername("admin").get();
        Assertions.assertNotNull(admin);

        User newUser = utils.makeUser();
        newUser.setDepartment(admin.getDepartment());
        userRepository.save(newUser);

        Shift shift = new Shift();
        shift.setUser(newUser);
        shift.setShiftType(ShiftType.SLOBODAN_DAN);
        shift.setStartTime(LocalDateTime.now().plusYears(1).minusDays(1).truncatedTo(ChronoUnit.DAYS));
        shift.setEndTime(shift.getStartTime().minusDays(1));
        shiftRepository.save(shift);
    }
    @When("User tries to update someone's days off, but he used all of them for next year")
    public void user_tries_to_update_someone_s_days_off_but_he_used_all_of_them_for_next_year() throws Exception {
        User admin = userRepository.findUserByUsername("admin").get();
        Assertions.assertNotNull(admin);

        User user = userRepository.findUserByUsername(utils.makeUser().getUsername()).get();
        Assertions.assertNotNull(user);

        resultActions = mvc.perform(put("/users/update-days-off/" + user.getLbz() + "?daysOff=0")
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying he used all of his days off for next year")
    public void bad_request_exception_is_thrown_with_status_code_saying_he_used_all_of_his_days_off_for_next_year(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("User tries to update someones days off")
    public void user_tries_to_update_someones_days_off() throws Exception {
        User admin = userRepository.findUserByUsername("admin").get();
        Assertions.assertNotNull(admin);

        User user = userRepository.findUserByUsername(utils.makeUser().getUsername()).get();
        Assertions.assertNotNull(user);

        resultActions = mvc.perform(put("/users/update-days-off/" + user.getLbz() + "?daysOff=5")
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("User's days off are updated in the database")
    public void user_s_days_off_are_updated_in_the_database() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.daysOff").value(5));

        User user = userRepository.findUserByUsername(utils.makeUser().getUsername()).get();
        userRepository.delete(user);
    }
}
