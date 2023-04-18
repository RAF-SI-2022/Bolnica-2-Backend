package com.raf.si.laboratoryservice.cucumber.labexamcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.laboratoryservice.cucumber.CucumberConfig;
import com.raf.si.laboratoryservice.cucumber.UtilsHelper;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.repository.ScheduledLabExamRepository;
import com.raf.si.laboratoryservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LabExamControllerStepsCount extends CucumberConfig {
    @Autowired
    private ScheduledLabExamRepository scheduledLabExamRepository;
    private Gson gson;
    private UtilsHelper util;
    private ResultActions resultActions;

    @Autowired
    private JwtUtil jwtUtil;

    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

    @When("doctor provides invalid information for fetching lab exams")
    public void doctor_provides_invalid_information_for_fetching_lab_exams() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lbp", "5a2e71bb-e4ee-43dd-a3ad-28e043f8b435");
        queryParams.add("dateFrom", String.valueOf(new Date()));

        resultActions = mvc.perform(get("/examination/scheduled-count").queryParams(queryParams)
                .header("Authorization", "Bearer " + util.generateToken())
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("BadRequestException is thrown with status code {int} for fetching lab exams")
    public void bad_request_exception_is_thrown_with_status_code_for_fetching_lab_exams(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("doctor provides valid information for fetching lab exams")
    public void doctor_provides_valid_information_for_fetching_lab_exams() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lbp", "c208f04d-9551-404e-8c54-9321f3ae9be8");
        queryParams.add("date", util.getDate());

        resultActions = mvc.perform(get("/examination/scheduled-count").queryParams(queryParams)
                .header("Authorization", "Bearer " + util.generateToken())
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("return lab exams")
    public void return_lab_exams() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));
    }
}
