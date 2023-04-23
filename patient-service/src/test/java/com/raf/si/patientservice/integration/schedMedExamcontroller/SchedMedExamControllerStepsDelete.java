package com.raf.si.patientservice.integration.schedMedExamcontroller;

import com.raf.si.patientservice.integration.CucumberConfig;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.repository.PatientRepository;
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


import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SchedMedExamControllerStepsDelete extends CucumberConfig {

    @Autowired
    private ScheduledMedExamRepository scheduledMedExamRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private Gson gson;
    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    private ResultActions resultAction;
    private Long id;

    @Before
    public  void  init(){
        util= new UtilsHelper(jwtUtil);
        gson= new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }



    @When("given scheduled medical exam id exists")
    public void given_scheduled_medical_exam_id_exists() throws Exception {
        ScheduledMedExamination scheduledMedExamination=util.createSchedMedExamination(patientRepository.findByLbp
                (UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8")).get());
        ScheduledMedExamination savedScheduledMedExam= scheduledMedExamRepository.save(scheduledMedExamination);
        assertNotNull(savedScheduledMedExam);
        id= savedScheduledMedExam.getId();

        resultAction= mvc.perform(delete(String.format("/sched-med-exam/delete/%s", id))
                .header("Authorization", "Bearer " + util.generateNurseToken())
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("deleted scheduled medical exam is returned")
    public void deleted_scheduled_medical_exam_is_returned() throws Exception {
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @When("Nurse tries to delete scheduled medical exam which id does not exists")
    public void nurse_tries_to_delete_scheduled_medical_exam_which_id_does_not_exists() throws Exception {
        resultAction= mvc.perform(delete(String.format("/sched-med-exam/delete/%s", 100L))
                .header("Authorization", "Bearer " + util.generateNurseToken())
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for given examination id")
    public void bad_request_exception_is_thrown_with_status_code_for_given_examination_id(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

}
