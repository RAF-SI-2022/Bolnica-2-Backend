package com.raf.si.laboratoryservice.cucumber.labexamcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.laboratoryservice.cucumber.CucumberConfig;
import com.raf.si.laboratoryservice.cucumber.UtilsHelper;
import com.raf.si.laboratoryservice.dto.request.CreateLabExamRequest;
import com.raf.si.laboratoryservice.dto.request.UpdateLabExamStatusRequest;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.repository.ScheduledLabExamRepository;
import com.raf.si.laboratoryservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LabExamControllerStepsUpdate extends CucumberConfig {

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

    @When("doctor provides invalid information for updating lab exam")
    public void doctor_provides_invalid_information_for_updating_lab_exam() throws Exception {
        UpdateLabExamStatusRequest updateLabExamStatusRequest = new UpdateLabExamStatusRequest();
        updateLabExamStatusRequest.setId(5L);
        updateLabExamStatusRequest.setStatus("Završeno");

        resultActions = mvc.perform(put("/examination/status")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(updateLabExamStatusRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for lab exam update")
    public void not_found_exception_is_thrown_with_status_code_for_lab_exam_update(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("doctor provides valid information for updating lab exam")
    public void doctor_provides_valid_information_for_updating_lab_exam() throws Exception {
        UpdateLabExamStatusRequest updateLabExamStatusRequest = new UpdateLabExamStatusRequest();
        updateLabExamStatusRequest.setId(1L);
        updateLabExamStatusRequest.setStatus("Završeno");

        resultActions = mvc.perform(put("/examination/status")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(updateLabExamStatusRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("return updated lab exam")
    public void return_updated_lab_exam() throws Exception {
        ScheduledLabExam scheduledLabExam = scheduledLabExamRepository.findById(1L).orElse(null);
        assertNotNull(scheduledLabExam);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value(scheduledLabExam.getNote()));
    }
}
