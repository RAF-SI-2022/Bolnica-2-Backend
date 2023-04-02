package com.raf.si.patientservice.integration.patientController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.repository.ScheduledMedExamRepository;
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


public class PatientControllerCreateSteps extends CucumberConfig {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private Gson gson;
    private UtilsHelper util;
    private ResultActions resultAction;

    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }


    @When("A valid request for creating a new patient is sent and the patient doesn't exist in the database")
    public void a_valid_request_for_creating_a_new_patient_is_sent_and_the_patient_doesn_t_exist_in_the_database() throws Exception{
        PatientRequest patientRequest = util.makePatientRequest();

        resultAction = mvc.perform(post("/patient/create")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(patientRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("A new patient is created and stored in the database")
    public void a_new_patient_is_created_and_stored_in_the_database() throws Exception{
        resultAction.andDo(MockMvcResultHandlers.print());

        Patient patient = patientRepository.findByJmbg(util.getPatientJmbg()).get();
        assertNotNull(patient);

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.jmbg").value(patient.getJmbg()))
                .andExpect(jsonPath("$.id").value(patient.getId()))
                .andExpect(jsonPath("$.lbp").value(patient.getLbp().toString()));
    }



    @When("Someone tries to create a new patient with the jmbg that already exists in the database")
    public void someone_tries_to_create_a_new_patient_with_the_jmbg_that_already_exists_in_the_database() throws Exception{
        PatientRequest patientRequest = util.makePatientRequest();

        resultAction = mvc.perform(post("/patient/create")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(patientRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient already exists")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_already_exists(Integer statusCode) throws Exception{
        resultAction.andExpect(status().is(statusCode));
    }
}
