package com.raf.si.patientservice.integration.patientController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PatientControllerUpdateSteps extends CucumberConfig {

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


    @When("A valid request for updating a patient is sent and the patient with that jmbg exists in the database")
    public void a_valid_request_for_updating_a_patient_is_sent_and_the_patient_with_that_jmbg_exists_in_the_database() throws Exception{
        PatientRequest patientRequest = util.makePatientRequest();
        patientRequest.setFirstName("TestFirstName");

        resultAction = mvc.perform(put("/patient/update")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(patientRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("Data for the patient with that jmbg is updated")
    public void data_for_the_patient_with_that_jmbg_is_updated() throws Exception{
        Patient patient = patientRepository.findByJmbg(util.getPatientJmbg()).get();
        assertNotNull(patient);

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.jmbg").value(patient.getJmbg()))
                .andExpect(jsonPath("$.firstName").value(patient.getFirstName()));
    }



    @When("Someone tries to update the patient data, but the patient with that jmbg doesn't exist")
    public void someone_tries_to_update_the_patient_data_but_the_patient_with_that_jmbg_doesn_t_exist() throws Exception{
        PatientRequest patientRequest = util.makePatientRequest();
        patientRequest.setJmbg("Doesn't exist");

        resultAction = mvc.perform(put("/patient/update")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(patientRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient with that jmbg doesn't exist and couldn't be updated")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_with_that_jmbg_doesn_t_exist_and_couldn_t_be_updated(Integer statusCode) throws Exception{
        resultAction.andExpect(status().is(statusCode));
    }



    @When("A valid request for updating a patient is sent and the patient with that lbp exists in the database")
    public void a_valid_request_for_updating_a_patient_is_sent_and_the_patient_with_that_lbp_exists_in_the_database() throws Exception{
        PatientRequest patientRequest = util.makePatientRequest();
        patientRequest.setLastName("TestLastName");
        Patient patient = patientRepository.findByJmbg(util.getPatientJmbg()).get();
        assertNotNull(patient);

        resultAction = mvc.perform(put(String.format("/patient/update/%s", patient.getLbp()))
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(patientRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("Data for the patient with that lbp is updated")
    public void data_for_the_patient_with_that_lbp_is_updated() throws Exception{
        Patient patient = patientRepository.findByJmbg(util.getPatientJmbg()).get();
        assertNotNull(patient);

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.jmbg").value(patient.getJmbg()))
                .andExpect(jsonPath("$.lastName").value(patient.getLastName()))
                .andExpect(jsonPath("$.lbp").value(patient.getLbp().toString()));
    }



    @When("Someone tries to update the patient data, but the patient with that lbp doesn't exist")
    public void someone_tries_to_update_the_patient_data_but_the_patient_with_that_lbp_doesn_t_exist() throws Exception{
        PatientRequest patientRequest = util.makePatientRequest();

        resultAction = mvc.perform(put(String.format("/patient/update/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(patientRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient with that lbp doesn't exist and couldn't be updated")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_with_that_lbp_doesn_t_exist_and_couldn_t_be_updated(Integer statusCode) throws Exception{
        resultAction.andExpect(status().is(statusCode));
    }
}
