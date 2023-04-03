package com.raf.si.patientservice.integration.healthRecordController;

import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthRecordControllerGetLightSteps extends CucumberConfig {

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


    @When("Someone tries to get the light information about a patient's health record and the patient exists in the database")
    public void someone_tries_to_get_the_light_information_about_a_patient_s_health_record_and_the_patient_exists_in_the_database() throws Exception{
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        resultAction = mvc.perform(get(String.format("/record/light/%s", patient.getLbp()))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("Return the light information about the patient's health record")
    public void return_the_light_information_about_the_patient_s_health_record() throws Exception{
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        HealthRecord healthRecord = patient.getHealthRecord();
        assertNotNull(healthRecord);

        resultAction.andDo(MockMvcResultHandlers.print());
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(healthRecord.getId()))
                .andExpect(jsonPath("$.patientLbp").value(patient.getLbp().toString()));
    }



    @When("Someone tries to get the light information about a patient's health record, but the patient with that lbp doesn't exist")
    public void someone_tries_to_get_the_light_information_about_a_patient_s_health_record_but_the_patient_with_that_lbp_doesn_t_exist() throws Exception{
        resultAction = mvc.perform(get(String.format("/record/light/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient with that lbp doesn't exist and the light health record information couldn't be returned")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_with_that_lbp_doesn_t_exist_and_the_light_health_record_information_couldn_t_be_returned(Integer statusCode) throws Exception{
        resultAction.andExpect(status().is(statusCode));
    }
}
