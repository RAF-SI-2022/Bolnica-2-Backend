package com.raf.si.userservice.cucumber.usercontroller;

import com.raf.si.userservice.cucumber.CucumberConfig;
import com.raf.si.userservice.cucumber.UtilsHelper;
import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Shift;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.ShiftType;
import com.raf.si.userservice.repository.DepartmentRepository;
import com.raf.si.userservice.repository.ShiftRepository;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.an.E;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerStepsGet extends CucumberConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    ShiftRepository shiftRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
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

        resultActions = mvc.perform(get("/users").queryParams(queryParams)
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("page with given parameters is returned containing users")
    public void page_with_given_parameters_is_returned_containing_users() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userList.size()", greaterThan(0)));
    }

    @When("tries to fetch all doctors")
    public void tries_to_fetch_all_doctors() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);

        resultActions = mvc.perform(get("/users/doctors")
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("list of doctors are returned")
    public void list_of_doctors_are_returned() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(0)));
    }

    @When("given department with pbo does not exist")
    public void given_department_with_pbo_does_not_exist() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);

        resultActions = mvc.perform(get(String.format("/users/doctors/%s", UUID.randomUUID().toString()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("NotFoundException is thrown with status code {int} for given pbo")
    public void not_found_exception_is_thrown_with_status_code_for_given_pbo(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("given department with pbo exists")
    public void given_department_with_pbo_exists() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);
        Department department = departmentRepository.findById(admin.getDepartment().getId()).orElse(null);
        assertNotNull(department);

        resultActions = mvc.perform(get(String.format("/users/doctors/%s", department.getPbo()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("list of doctors for given department is returned")
    public void list_of_doctors_for_given_department_is_returned() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(0)));
    }

    @When("User tries to update covid access but doesn't have permission")
    public void user_tries_to_update_covid_access_but_doesn_t_have_permission() throws Exception {
        User admin = userRepository.findUserByUsername("admin").get();
        assertNotNull(admin);

        resultActions = mvc.perform(put(String.format("/users/update-covid-access/%s?covidAccess=true", UUID.fromString("56776899-ae24-431a-818a-e5424683bf3c")))
                .header("Authorization", "Bearer " + utils.generateToken(admin, new String[] {"ROLE_VISA_MED_SESTRA"}))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for given user")
    public void bad_request_exception_is_thrown_with_status_code_for_given_user(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("User tries to update covid access for someone")
    public void user_tries_to_update_covid_access_for_someone() throws Exception {
        User admin = userRepository.findUserByUsername("admin").get();
        assertNotNull(admin);

        resultActions = mvc.perform(put(String.format("/users/update-covid-access/%s?covidAccess=true", admin.getLbz()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("User's covid access is updated in the database")
    public void user_s_covid_access_is_updated_in_the_database() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.covidAccess").value(true));
    }

    @When("User tries to get his subordinates, but he doesn't have any")
    public void user_tries_to_get_his_subordinates_but_he_doesn_t_have_any() throws Exception {
        User user = userRepository.findUserByLbz(UUID.fromString("f581b31f-adff-4a34-b9f3-32b3502310f1")).get();
        assertNotNull(user);

        resultActions = mvc.perform(get("/users/subordinates")
                .header("Authorization", "Bearer " + utils.generateToken(user))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("NotFoundException is thrown with status code {int} for given user's lbz")
    public void not_found_exception_is_thrown_with_status_code_for_given_user_s_lbz(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("User tries to get his subordinates and he has subordinates")
    public void user_tries_to_get_his_subordinates_and_he_has_subordinates() throws Exception {
        User admin = userRepository.findUserByUsername("admin").get();
        assertNotNull(admin);

        resultActions = mvc.perform(get("/users/subordinates")
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("User's subordinates are returned")
    public void user_s_subordinates_are_returned() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2));
    }

    @Given("Someone who used all days off for next year")
    public void someone_who_used_all_days_off_for_next_year() {
        User admin = userRepository.findUserByUsername("admin").get();
        assertNotNull(admin);

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
        assertNotNull(admin);

        User user = userRepository.findUserByUsername(utils.makeUser().getUsername()).get();
        assertNotNull(user);

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
        assertNotNull(admin);

        User user = userRepository.findUserByUsername(utils.makeUser().getUsername()).get();
        assertNotNull(user);

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
