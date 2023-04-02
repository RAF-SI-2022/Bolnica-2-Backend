package com.raf.si.patientservice.integration.patientController;

import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PatientControllerGetSteps extends CucumberConfig {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    private ResultActions resultAction;

    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
    }


    @When("Someone tries to get the information for a patient, and the patient with that lbp exists in the database")
    public void someone_tries_to_get_the_information_for_a_patient_and_the_patient_with_that_lbp_exists_in_the_database() throws Exception{
        Patient patient = patientRepository.findByJmbg(util.getPatientJmbg()).get();
        assertNotNull(patient);

        resultAction = mvc.perform(get(String.format("/patient/%s", patient.getLbp()))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("Return the information about that patient")
    public void return_the_information_about_that_patient() throws Exception{
        Patient patient = patientRepository.findByJmbg(util.getPatientJmbg()).get();
        assertNotNull(patient);

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.jmbg").value(patient.getJmbg()))
                .andExpect(jsonPath("$.id").value(patient.getId()))
                .andExpect(jsonPath("$.lbp").value(patient.getLbp().toString()));
    }



    @When("Someone tries to get information about a patient, but a patient with that lbp doesn't exist in the database")
    public void someone_tries_to_get_information_about_a_patient_but_a_patient_with_that_lbp_doesn_t_exist_in_the_database() throws Exception{
        resultAction = mvc.perform(get(String.format("/patient/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient with that lbp doesn't exist and the information couldn't be returned")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_with_that_lbp_doesn_t_exist_and_the_information_couldn_t_be_returned(Integer statusCode) throws Exception{
        resultAction.andExpect(status().is(statusCode));
    }



    @When("Someone tries to get the information about all patients that match the given criteria")
    public void someone_tries_to_get_the_information_about_all_patients_that_match_the_given_criteria() throws Exception{
        Patient patient = patientRepository.findByJmbg(util.getPatientJmbg()).get();
        assertNotNull(patient);

        String uri = String.format("/patient?lbp=%s&jmbg=%s&firstName=%s&lastName=%s",
                patient.getLbp(),
                patient.getJmbg(),
                patient.getFirstName(),
                patient.getLastName());

        resultAction = mvc.perform(get(uri)
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("Return all patients that matched the required criteria")
    public void return_all_patients_that_matched_the_required_criteria() throws Exception{
        Patient patient = patientRepository.findByJmbg(util.getPatientJmbg()).get();
        assertNotNull(patient);

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }
}
