package com.raf.si.patientservice.integration.healthRecordController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.AddVaccinationRequest;
import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.request.UpdateHealthRecordRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.Vaccination;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.Status;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.time.DateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthRecordControllerUpdateHealthRecordSteps extends CucumberConfig {


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


    @When("someone tries to set blood type to {string} and rhfactor to {string} for patient {string}")
    public void someone_tries_to_set_blood_type_to_and_rhfactor_to_for_patient(String bloodType, String rhFactor, String patientName) throws Exception {
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        HealthRecord healthRecord = patient.getHealthRecord();
        assertNotNull(healthRecord);

        UpdateHealthRecordRequest updateHealthRecordRequest = new UpdateHealthRecordRequest();
        updateHealthRecordRequest.setRhfactor(rhFactor);
        updateHealthRecordRequest.setBlodtype(bloodType);

        resultAction = mvc.perform(put(String.format("/record/%s", patient.getLbp().toString()))
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(updateHealthRecordRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("Return basic information about patient's health record which says that bloodtype is {string} and rhfactor is {string}")
    public void return_basic_information_about_patient_s_health_record_which_says_that_bloodtype_is_and_rhfactor_is(String bloodType, String rhFactor) throws Exception {
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        HealthRecord healthRecord = patient.getHealthRecord();
        assertNotNull(healthRecord);

        resultAction.andDo(MockMvcResultHandlers.print());
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(healthRecord.getId().toString()))
                .andExpect(jsonPath("$.bloodType").value(bloodType))
                .andExpect(jsonPath("$.rhFactor.notation").value(rhFactor))
                .andExpect(jsonPath("$.patientLbp").value(patient.getLbp().toString()));
    }


    @Then("BadRequestException is thrown with status code {int} saying {string}")
    public void bad_request_exception_is_thrown_with_status_code_saying(Integer statusCode, String message) throws Exception {
        resultAction.andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.errorMessage").value(message));
    }

    @When("Someone tries to add vaccination {string} to user's healthrecord that happened now")
    public void someone_tries_to_add_vaccination_to_user_s_healthrecord_that_happened_now(String vaccine) throws Exception {
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        HealthRecord healthRecord = patient.getHealthRecord();
        assertNotNull(healthRecord);

        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine(vaccine);
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //LocalDate localDate = LocalDate.parse(patient.getBirthDate().toString()).plusDays(1).toString();
        addVaccinationRequest.setDate(new Date(System.currentTimeMillis() - 3600 * 1000*25));

        resultAction = mvc.perform(put(String.format("/record/add-vaccination/%s", patient.getLbp().toString()))
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(addVaccinationRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("Return extended information about added created vaccination and vaccination count for that user")
    public void return_extended_information_about_added_created_vaccination_and_vaccination_count_for_that_user() throws Exception {
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        HealthRecord healthRecord = patient.getHealthRecord();
        assertNotNull(healthRecord);

        String vaccine = util.getVaccineBootstrap();

        resultAction.andDo(MockMvcResultHandlers.print());
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.vaccinationResponse.vaccine.name").value(vaccine))
                .andExpect(jsonPath("$.vaccinationResponse.healthRecordId").value(healthRecord.getId().toString()));
    }

    @When("Someone tries to add vaccination {string} to user's healthrecord that happened on {string}")
    public void someone_tries_to_add_vaccination_to_user_s_healthrecord_that_happened_on(String vaccine, String dateString) throws Exception {
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(patient);

        HealthRecord healthRecord = patient.getHealthRecord();
        assertNotNull(healthRecord);

        AddVaccinationRequest addVaccinationRequest = new AddVaccinationRequest();
        addVaccinationRequest.setVaccine(util.getVaccineBootstrap());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        addVaccinationRequest.setDate(formatter.parse(dateString));

        resultAction = mvc.perform(put(String.format("/record/add-vaccination/%s", patient.getLbp().toString()))
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(addVaccinationRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }


}
