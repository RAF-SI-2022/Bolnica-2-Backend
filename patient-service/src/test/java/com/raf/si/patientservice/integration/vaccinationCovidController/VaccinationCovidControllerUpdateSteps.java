package com.raf.si.patientservice.integration.vaccinationCovidController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.localTypeDefAdapters.LocalDateTimeAdapter;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.model.ScheduledVaccinationCovid;
import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.repository.ScheduledVaccinationCovidRepository;
import com.raf.si.patientservice.repository.VaccinationCovidRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VaccinationCovidControllerUpdateSteps extends CucumberConfig {


    @Autowired
    private ScheduledVaccinationCovidRepository scheduledVaccinationCovidRepository;
    @Autowired
    private VaccinationCovidRepository vaccinationCovidRepository;
    @Autowired
    private Gson gson;
    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    private ResultActions resultAction;

    @Before
    public  void  init(){
        util= new UtilsHelper(jwtUtil);
        gson= new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }

    @When("Nurse tries to update scheduled vaccination status but new values are not given")
    public void nurse_tries_to_update_scheduled_vaccination_status_but_new_values_are_not_given() throws Exception {
        ScheduledVaccinationCovid scheduledVaccinationCovid = scheduledVaccinationCovidRepository.findById(1L).orElse(null);
        assertNotNull(scheduledVaccinationCovid);

        resultAction = mvc.perform(patch("/vaccination/scheduled/change-status/"+ 1L)
                .header("Authorization", "Bearer " + util.generateNurseTokenValid()));
    }
    @Then("BadRequestException is thrown with status code {int} for missing params covid")
    public void bad_request_exception_is_thrown_with_status_code_for_missing_params_covid(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse tries to update scheduled vaccination status but given Examination status is unknown")
    public void nurse_tries_to_update_scheduled_vaccination_status_but_given_examination_status_is_unknown() throws Exception {
        ScheduledVaccinationCovid scheduledVaccinationCovid = scheduledVaccinationCovidRepository.findById(1L).orElse(null);
        assertNotNull(scheduledVaccinationCovid);

        String status ="?vaccStatus=oiewiojrmpoaerw";
        resultAction = mvc.perform(patch("/vaccination/scheduled/change-status/"+ 1L+status)
                .header("Authorization", "Bearer " + util.generateNurseTokenValid()));
    }
    @Then("BadRequestException is thrown with status code {int} for uknown Examination status covid")
    public void bad_request_exception_is_thrown_with_status_code_for_uknown_examination_status_covid(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse tries to update scheduled vaccination status but given Patient arrival status is unknown")
    public void nurse_tries_to_update_scheduled_vaccination_status_but_given_patient_arrival_status_is_unknown() throws Exception {
        ScheduledVaccinationCovid scheduledVaccinationCovid = scheduledVaccinationCovidRepository.findById(1L).orElse(null);
        assertNotNull(scheduledVaccinationCovid);

        String status ="?patientArrivalStatus=oiewiojrmpoaerw";
        resultAction = mvc.perform(patch("/vaccination/scheduled/change-status/"+ 1L+status)
                .header("Authorization", "Bearer " + util.generateNurseTokenValid()));
    }
    @Then("BadRequestException is thrown with status code {int} for unknown Patient arrival status covid")
    public void bad_request_exception_is_thrown_with_status_code_for_unknown_patient_arrival_status_covid(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse provides valid information to update scheduled vaccination status covid")
    public void nurse_provides_valid_information_to_update_scheduled_vaccination_status_covid() throws Exception {
        ScheduledVaccinationCovid scheduledVaccinationCovid = scheduledVaccinationCovidRepository.findById(1L).orElse(null);
        assertNotNull(scheduledVaccinationCovid);

        String status ="?vaccStatus=U toku";
        resultAction = mvc.perform(patch("/vaccination/scheduled/change-status/"+ 1L+status)
                .header("Authorization", "Bearer " + util.generateNurseTokenValid()));
    }
    @Then("Scheduled vaccination covid gets updated")
    public void scheduled_vaccination_covid_gets_updated() throws Exception {
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.testStatus.examinationStatus").value("U toku"));
    }







}
