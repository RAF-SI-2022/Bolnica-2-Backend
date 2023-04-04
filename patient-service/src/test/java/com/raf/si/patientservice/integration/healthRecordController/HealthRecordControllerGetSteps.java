package com.raf.si.patientservice.integration.healthRecordController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.UpdateHealthRecordRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.Vaccine;
import com.raf.si.patientservice.repository.AllergenRepository;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.repository.VaccineRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthRecordControllerGetSteps extends CucumberConfig {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private AllergenRepository allergenRepository;

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

    @When("Someone tries to get the information about a patient's health record and the patient exists in the database")
    public void someone_tries_to_get_the_information_about_a_patient_s_health_record_and_the_patient_exists_in_the_database() throws Exception{
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        resultAction = mvc.perform(get(String.format("/record/%s", patient.getLbp()))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("Return the information about the patient's health record")
    public void return_the_information_about_the_patient_s_health_record() throws Exception{
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        HealthRecord healthRecord = patient.getHealthRecord();
        assertNotNull(healthRecord);

        resultAction.andDo(MockMvcResultHandlers.print());
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(healthRecord.getId()))
                .andExpect(jsonPath("$.patientLbp").value(patient.getLbp().toString()));
    }



    @When("Someone tries to get the information about a patient's health record, but the patient with that lbp doesn't exist")
    public void someone_tries_to_get_the_information_about_a_patient_s_health_record_but_the_patient_with_that_lbp_doesn_t_exist() throws Exception{
        resultAction = mvc.perform(get(String.format("/record/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient with that lbp doesn't exist and the health record information couldn't be returned")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_with_that_lbp_doesn_t_exist_and_the_health_record_information_couldn_t_be_returned(Integer statusCode) throws Exception{
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Someone tries to get all vaccines available in database")
    public void someone_tries_to_get_all_vaccines_available_in_database() throws Exception {
        // Write code here that turns the phrase above into concrete actions

        resultAction = mvc.perform(get("/record/allergens")
                .header("Authorization", "Bearer " + util.generateToken()));
    }

    @Then("Return all vaccines available in database")
    public void return_all_vaccines_available_in_database() throws Exception {
        resultAction.andExpect(status().isOk());
    }


    @When("Someone tries to get all allergens available in database")
    public void someone_tries_to_get_all_allergens_available_in_database() throws Exception {
        resultAction = mvc.perform(get("/record/vaccines")
                .header("Authorization", "Bearer " + util.generateToken()));
    }

    @Then("Return all allergens available in database")
    public void return_all_allergens_available_in_database() throws Exception {
        resultAction.andExpect(status().isOk());
    }


}
