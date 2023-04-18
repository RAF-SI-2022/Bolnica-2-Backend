package com.raf.si.laboratoryservice.cucumber.labexamcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.laboratoryservice.cucumber.CucumberConfig;
import com.raf.si.laboratoryservice.cucumber.UtilsHelper;
import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.repository.ScheduledLabExamRepository;
import com.raf.si.laboratoryservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LabExamControllerStepsCreate extends CucumberConfig {
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

    @When("doctor provides invalid information for creating a lab exam")
    public void doctor_provides_invalid_information_for_creating_a_lab_exam() throws Exception {
        CreateLabExamRequest createLabExamRequest = new CreateLabExamRequest();
        createLabExamRequest.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));

        resultActions = mvc.perform(post("/examination/create")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(createLabExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("BadRequestException is thrown with status code {int} for creating lab exam")
    public void bad_request_exception_is_thrown_with_status_code_for_creating_lab_exam(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("doctor provides valid information for creating a lab exam")
    public void doctor_provides_valid_information_for_creating_a_lab_exam() throws Exception {
        CreateLabExamRequest createLabExamRequest = util.createLabExamRequest();

        resultActions = mvc.perform(post("/examination/create")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(createLabExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("new lab exam is returned")
    public void new_lab_exam_is_returned() throws Exception {
        ScheduledLabExam scheduledLabExam = scheduledLabExamRepository.findById(2L).orElse(null);
        assertNotNull(scheduledLabExam);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value(scheduledLabExam.getNote()));
    }


}
