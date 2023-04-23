package com.raf.si.laboratoryservice.cucumber.labexamcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.laboratoryservice.cucumber.CucumberConfig;
import com.raf.si.laboratoryservice.cucumber.UtilsHelper;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.repository.ScheduledLabExamRepository;
import com.raf.si.laboratoryservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LabExamControllerStepsGet extends CucumberConfig {

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

    @When("lab exam does not exist for given id")
    public void lab_exam_does_not_exist_for_given_id() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("dateFrom", util.getDate());
        queryParams.add("lbp", "5a2e71bb-e4ee-43dd-a3ad-28e043f8b435");

        resultActions = mvc.perform(get("/examination/scheduled").queryParams(queryParams)
                .header("Authorization", "Bearer " + util.generateToken())
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for exam with id")
    public void not_found_exception_is_thrown_with_status_code_for_exam_with_id(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }


    @When("given lab exam exists for given id")
    public void given_lab_exam_exists_for_given_id() throws Exception {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date newDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(newDate);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lbp", "c208f04d-9551-404e-8c54-9321f3ae9be8");
        queryParams.add("dateFrom", dateString);

        resultActions = mvc.perform(get("/examination/scheduled").queryParams(queryParams)
                .header("Authorization", "Bearer " + util.generateToken())
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("lab exam is returned for given id")
    public void lab_exam_is_returned_for_given_id() throws Exception {
        ScheduledLabExam scheduledLabExam = scheduledLabExamRepository.findById(1L).orElse(null);
        assertNotNull(scheduledLabExam);
        MvcResult result = resultActions.andReturn();
        MockHttpServletResponse response = result.getResponse();
        String responseBody = response.getContentAsString();
        System.out.println("Response body: " + responseBody);

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].note").value(scheduledLabExam.getNote()));

    }
}
