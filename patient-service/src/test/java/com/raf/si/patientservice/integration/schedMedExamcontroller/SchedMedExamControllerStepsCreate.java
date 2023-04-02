package com.raf.si.patientservice.integration.schedMedExamcontroller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class SchedMedExamControllerStepsCreate extends CucumberConfig {

    @Autowired
    private ScheduledMedExamRepository scheduledMedExamRepository;
    @Autowired
    private Gson gson;
    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    @Value("${duration.of.exam}")
    private int DURATION_OF_EXAM;
    private ResultActions resultAction;

    @Before
    public  void  init(){
        util= new UtilsHelper(jwtUtil, DURATION_OF_EXAM);
        gson= new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

    @When("Nurse provides valid information")
    public void nurse_provides_valid_information() throws Exception {
        SchedMedExamRequest schedMedExamRequest= util.createSchedMedExamRequest(1);

        resultAction= mvc.perform(post("/sched-med-exam/create")
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .content(gson.toJson(schedMedExamRequest))
                .contentType(MediaType.APPLICATION_JSON));

    }
    @Then("created scheduled medical exam is returned")
    public void created_scheduled_medical_exam_is_returned() throws Exception {
        List<ScheduledMedExamination> createdScheduledMedExamination= scheduledMedExamRepository
                .findByLbzDoctor(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435")).get();
        assertNotNull(createdScheduledMedExamination);
        resultAction.andDo(MockMvcResultHandlers.print());
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdScheduledMedExamination
                        .get(createdScheduledMedExamination.size()-1).getId()));
    }

    @When("Nurse tries to create a new scheduled medical exam for doctor who has uncompleted exams")
    public void nurse_tries_to_create_a_new_scheduled_medical_exam_for_doctor_who_has_uncompleted_exams() throws Exception {
        SchedMedExamRequest schedMedExamRequest= util.createSchedMedExamRequest(0);

        resultAction= mvc.perform(post("/sched-med-exam/create")
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .content(gson.toJson(schedMedExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("BadRequestException is thrown with status code {int}")
    public void bad_request_exception_is_thrown_with_status_code(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse tries to create a new scheduled medical exam for patient which does not exists")
    public void nurse_tries_to_create_a_new_scheduled_medical_exam_for_patient_which_does_not_exists() throws Exception {
        SchedMedExamRequest schedMedExamRequest= util.createSchedMedExamRequest(1);
        schedMedExamRequest.setLbp(UUID.fromString("34015c72-cf51-11ed-afa1-0242ac120002"));

        resultAction= mvc.perform(post("/sched-med-exam/create")
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .content(gson.toJson(schedMedExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for given patient")
    public void bad_request_exception_is_thrown_with_status_code_for_given_patient(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }
}
