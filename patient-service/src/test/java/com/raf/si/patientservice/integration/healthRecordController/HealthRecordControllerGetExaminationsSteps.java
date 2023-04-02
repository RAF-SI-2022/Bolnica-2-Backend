package com.raf.si.patientservice.integration.healthRecordController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.MedicalExaminationFilterRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.MedicalExamination;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthRecordControllerGetExaminationsSteps extends CucumberConfig {

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



    @When("Someone tries to get all medical examinations for a patient and the patient exists in the database")
    public void someone_tries_to_get_all_medical_examinations_for_a_patient_and_the_patient_exists_in_the_database() throws Exception{
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        MedicalExaminationFilterRequest request = makeMedicalExaminationFilterRequest();

        resultAction = mvc.perform(post(String.format("/record/examinations/%s", patient.getLbp()))
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Transactional
    @Then("Return medical examinations that match the criteria")
    public void return_medical_examinations_that_match_the_criteria() throws Exception{
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        List<MedicalExamination> examinations = patient.getHealthRecord().getMedicalExaminations();

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(examinations.size()));
    }



    @When("Someone tries to get all medical examinations for a patient, but the patient with that lbp doesn't exist in the database")
    public void someone_tries_to_get_all_medical_examinations_for_a_patient_but_the_patient_with_that_lbp_doesn_t_exist_in_the_database() throws Exception{
        MedicalExaminationFilterRequest request = makeMedicalExaminationFilterRequest();

        resultAction = mvc.perform(post(String.format("/record/examinations/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient with that lbp doesn't exist and the medical examinations couldn't be returned")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_with_that_lbp_doesn_t_exist_and_the_medical_examinations_couldn_t_be_returned(Integer statusCode) throws Exception{
        resultAction.andExpect(status().is(statusCode));
    }



    private MedicalExaminationFilterRequest makeMedicalExaminationFilterRequest() throws Exception{
        MedicalExaminationFilterRequest request = new MedicalExaminationFilterRequest();

        SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
        Date startDate = format.parse("10-10-2000");
        Date endDate = format.parse("10-10-2100");

        request.setStartDate(startDate);
        request.setEndDate(endDate);

        return request;
    }
}
