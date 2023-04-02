package com.raf.si.patientservice.integration.schedMedExamcontroller;

import com.raf.si.patientservice.integration.CucumberConfig;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.repository.ScheduledMedExamRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import  org.hamcrest.Matchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SchedMedExamControllerStepsGet extends CucumberConfig{

    @Autowired
    private ScheduledMedExamRepository scheduledMedExamRepository;
    @Autowired
    private Gson gson;
    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    private ResultActions resultAction;
    @Value("${duration.of.exam}")
    private int DURATION_OF_EXAM;

    @Before
    public  void  init(){
        util= new UtilsHelper(jwtUtil, DURATION_OF_EXAM);
        gson= new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }
    @When("Nurse gives valid information for search")
    public void nurse_gives_valid_information_for_search() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lbz","5a2e71bb-e4ee-43dd-a3ad-28e043f8b435");

        resultAction = mvc.perform(get("/sched-med-exam/search").queryParams(queryParams)
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("Nurse gets list of scheduled medical exam for doctor")
    public void nurse_gets_list_of_scheduled_medical_exam_for_doctor() throws Exception {
        resultAction.andExpect(status().isOk());
    }

    @When("Nurse tries to get scheduled medical exam for a doctor which lbz id does not exists")
    public void nurse_tries_to_get_scheduled_medical_exam_for_a_doctor_which_lbz_id_does_not_exists() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lbz","6e3d88ee-e4ee-43dd-a3ad-28e043f8b435");

        resultAction = mvc.perform(get("/sched-med-exam/search").queryParams(queryParams)
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for doctor lbz id which id does not exists")
    public void bad_request_exception_is_thrown_with_status_code_for_doctor_lbz_id_which_id_does_not_exists(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

}
