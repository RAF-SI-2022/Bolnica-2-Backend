package com.raf.si.userservice.cucumber.departmentcontroller;

import com.raf.si.userservice.cucumber.CucumberConfig;
import com.raf.si.userservice.cucumber.UtilsHelper;
import com.raf.si.userservice.model.Hospital;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.repository.DepartmentRepository;
import com.raf.si.userservice.repository.HospitalRepository;
import com.raf.si.userservice.repository.UserRepository;
import com.raf.si.userservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DepartmentControllerSteps extends CucumberConfig {

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private HospitalRepository hospitalRepository;
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

    @When("hospital does not exist for given pbb")
    public void hospital_does_not_exist_for_given_pbb() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);

        resultActions = mvc.perform(get(String.format("/departments/%s", UUID.randomUUID().toString()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for given hospital")
    public void not_found_exception_is_thrown_with_status_code_for_given_hospital(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("hospital exists for given pbb")
    public void hospital_exists_for_given_pbb() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        Hospital hospital = hospitalRepository.findById(1L).orElse(null);
        assertNotNull(admin);
        assertNotNull(hospital);

        resultActions = mvc.perform(get(String.format("/departments/%s", hospital.getPbb()))
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("departments for given hospital are returned")
    public void departments_for_given_hospital_are_returned() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(0)));
    }

    @When("user request all departments")
    public void user_request_all_departments() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);

        resultActions = mvc.perform(get("/departments")
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("all departments are returned")
    public void all_departments_are_returned() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(0)));
    }

    @When("user request all hospitals")
    public void user_request_all_hospitals() throws Exception {
        User admin = userRepository.findUserByUsername("admin").orElse(null);
        assertNotNull(admin);

        resultActions = mvc.perform(get("/departments/hospitals")
                .header("Authorization", "Bearer " + utils.generateToken(admin))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("all hospitals are returned")
    public void all_hospitals_are_returned() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(0)));
    }
}
