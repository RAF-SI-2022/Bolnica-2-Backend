package com.raf.si.patientservice.integration.schedMedExamcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.repository.ScheduledMedExamRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;




import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class SchedMedExamControllerStepsUpdate extends CucumberConfig {

    @Autowired
    private ScheduledMedExamRepository scheduledMedExamRepository;
    @Autowired
    private Gson gson;
    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    @Value("${duration.of.exam}")
    private int DURATION_OF_EXAM;
    private ResultActions resultAction;

    @Before
    public void init() {
        util = new UtilsHelper(jwtUtil, DURATION_OF_EXAM);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }


    @When("Doctor provied valid information")
    public void doctor_provied_valid_information() throws Exception {
        ScheduledMedExamination scheduledMedExamination = scheduledMedExamRepository.findById(1L).orElse(null);
        assertNotNull(scheduledMedExamination);

        UpdateSchedMedExamRequest updateSchedMedExamRequest = util.createUpdateSchedMedExamRequest("U toku");
        resultAction = mvc.perform(put("/sched-med-exam/update-exam-status")
                .header("Authorization", "Bearer " + util.generateDocaToken())
                .content(gson.toJson(updateSchedMedExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("updated scheduled medical exam is returned")
    public void updated_scheduled_medical_exam_is_returned() throws Exception {
        UpdateSchedMedExamRequest updateSchedMedExamRequest = util.createUpdateSchedMedExamRequest("U toku");
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updateSchedMedExamRequest.getId()));
    }

    @When("Doctor tries to update the examination status of a scheduled medical exam which exam id does not exists")
    public void doctor_tries_to_update_the_examination_status_of_a_scheduled_medical_exam_which_exam_id_does_not_exists() throws Exception {
        UpdateSchedMedExamRequest updateSchedMedExamRequest = util.createUpdateSchedMedExamRequest("U toku");
        updateSchedMedExamRequest.setId(10L);

        resultAction = mvc.perform(put("/sched-med-exam/update-exam-status")
                .header("Authorization", "Bearer " + util.generateDocaToken())
                .content(gson.toJson(updateSchedMedExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("BadRequestException is thrown with status code {int} for given exam id")
    public void bad_request_exception_is_thrown_with_status_code_for_given_exam_id(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Doctor tries to update the examination status of a scheduled medical exam to unidentified exam status")
    public void doctor_tries_to_update_the_examination_status_of_a_scheduled_medical_exam_to_unidentified_exam_status() throws Exception {
        UpdateSchedMedExamRequest updateSchedMedExamRequest = util.createUpdateSchedMedExamRequest("U toku");
        updateSchedMedExamRequest.setNewStatus("foo");

        resultAction = mvc.perform(put("/sched-med-exam/update-exam-status")
                .header("Authorization", "Bearer " + util.generateDocaToken())
                .content(gson.toJson(updateSchedMedExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("BadRequestException is thrown with status code {int} for unidentified exam status")
    public void bad_request_exception_is_thrown_with_status_code_for_unidentified_exam_status(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Doctor tries to update the examination status of a scheduled medical exam to forbidden exam status")
    public void doctor_tries_to_update_the_examination_status_of_a_scheduled_medical_exam_to_forbidden_exam_status() throws Exception {
        UpdateSchedMedExamRequest updateSchedMedExamRequest = util.createUpdateSchedMedExamRequest("Otkazano");

        resultAction = mvc.perform(put("/sched-med-exam/update-exam-status")
                .header("Authorization", "Bearer " + util.generateDocaToken())
                .content(gson.toJson(updateSchedMedExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("BadRequestException is thrown with status code {int} for forbidden exam status")
    public void bad_request_exception_is_thrown_with_status_code_for_forbidden_exam_status(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse provides valid information for update")
    public void nurse_provides_valid_information_for_update() throws Exception {
        ScheduledMedExamination scheduledMedExamination = scheduledMedExamRepository.findById(1L).orElse(null);
        assertNotNull(scheduledMedExamination);

        UpdateSchedMedExamRequest updateSchedMedExamRequest = util.createUpdateSchedMedExamRequest("Čeka");
        resultAction = mvc.perform(put("/sched-med-exam/update-patient-arrival-status")
                .header("Authorization", "Bearer " + util.generateNurseToken())
                .content(gson.toJson(updateSchedMedExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("updated scheduled medical exam is returned with code {int}")
    public void updated_scheduled_medical_exam_is_returned_with_code(Integer statusCode) throws Exception {
        UpdateSchedMedExamRequest updateSchedMedExamRequest = util.createUpdateSchedMedExamRequest("Čeka");
        resultAction.andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.id").value(updateSchedMedExamRequest.getId()));
    }

    @When("Nurse tries to update the patient arrival status of a scheduled medical exam which exam id does not exists")
    public void nurse_tries_to_update_the_patient_arrival_status_of_a_scheduled_medical_exam_which_exam_id_does_not_exists() throws Exception {
        UpdateSchedMedExamRequest updateSchedMedExamRequest = util.createUpdateSchedMedExamRequest("Čeka");
        updateSchedMedExamRequest.setId(10L);

        resultAction = mvc.perform(put("/sched-med-exam/update-patient-arrival-status")
                .header("Authorization", "Bearer " + util.generateNurseToken())
                .content(gson.toJson(updateSchedMedExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for given examination id of update operation")
    public void bad_request_exception_is_thrown_with_status_code_for_given_examination_id_of_update_operation(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse tries to update the patient arrival status of a scheduled medical exam to unidentified exam status")
    public void nurse_tries_to_update_the_patient_arrival_status_of_a_scheduled_medical_exam_to_unidentified_exam_status() throws Exception {
        UpdateSchedMedExamRequest updateSchedMedExamRequest = util.createUpdateSchedMedExamRequest("foo");

        resultAction = mvc.perform(put("/sched-med-exam/update-patient-arrival-status")
                .header("Authorization", "Bearer " + util.generateNurseToken())
                .content(gson.toJson(updateSchedMedExamRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for unidentified exam status of update operation")
    public void bad_request_exception_is_thrown_with_status_code_for_unidentified_exam_status_of_update_operation(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

}
