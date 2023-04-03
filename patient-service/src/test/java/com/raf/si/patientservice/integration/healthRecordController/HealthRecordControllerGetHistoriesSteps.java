package com.raf.si.patientservice.integration.healthRecordController;

import com.raf.si.patientservice.dto.request.MedicalExaminationFilterRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.MedicalExamination;
import com.raf.si.patientservice.model.MedicalHistory;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthRecordControllerGetHistoriesSteps extends CucumberConfig {

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


    @When("Someone tries to get all medical histories for a patient and the patient exists in the database")
    public void someone_tries_to_get_all_medical_histories_for_a_patient_and_the_patient_exists_in_the_database() throws Exception{
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        String uri = String.format("/record/history/%s?mkb10=djovak", patient.getLbp());

        resultAction = mvc.perform(get(uri)
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Transactional
    @Then("Return medical histories that match the criteria")
    public void return_medical_histories_that_match_the_criteria() throws Exception{
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        List<MedicalHistory> historyList = patient.getHealthRecord().getMedicalHistory();

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(historyList.size()));
    }



    @When("Someone tries to get all medical histories for a patient, but the patient with that lbp doesn't exist in the database")
    public void someone_tries_to_get_all_medical_histories_for_a_patient_but_the_patient_with_that_lbp_doesn_t_exist_in_the_database() throws Exception{
        resultAction = mvc.perform(get(String.format("/record/history/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient with that lbp doesn't exist and the medical histories couldn't be returned")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_with_that_lbp_doesn_t_exist_and_the_medical_histories_couldn_t_be_returned(Integer statusCode) throws Exception{
        resultAction.andExpect(status().is(statusCode));
    }
}
