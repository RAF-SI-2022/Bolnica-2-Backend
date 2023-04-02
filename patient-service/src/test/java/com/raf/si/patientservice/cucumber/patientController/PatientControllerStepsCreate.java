package com.raf.si.patientservice.cucumber.patientController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.cucumber.CucumberConfig;
import com.raf.si.patientservice.cucumber.UtilsHelper;
import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PatientControllerStepsCreate extends CucumberConfig {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private ResultActions resultActions;
    private Gson gson;
    private UtilsHelper util;

    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }


    @When("A valid request for creating a patient is given and the patient doesn't already exist")
    public void a_valid_request_for_creating_a_patient_is_given_and_the_patient_doesn_t_already_exist() throws Exception{
        PatientRequest patientRequest = util.makePatientRequest();
        resultActions = mvc.perform(post("/patient/create")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(patientRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("Return the created patient")
    public void return_the_created_patient() throws Exception {
        Patient patient = patientRepository.findByJmbg("1342002345612").orElse(null);
        assertNotNull(patient);
        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.lbp").value(patient.getLbp()))
                .andExpect(jsonPath("$.jmbg").value(patient.getJmbg()));
    }
}
