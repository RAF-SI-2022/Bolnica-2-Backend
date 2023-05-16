package com.raf.si.patientservice.integration.hospitalizationController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.model.Hospitalization;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.HospitalRoomRepository;
import com.raf.si.patientservice.repository.HospitalizationRepository;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HospitalizationControllerSteps extends CucumberConfig {

    @Autowired
    private HospitalizationRepository hospitalizationRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private HospitalRoomRepository hospitalRoomRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    private Gson gson;
    private ResultActions resultAction;


    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

    @When("User tries to make a hospitalizaiton for a room that doesn't exist")
    public void user_tries_to_make_a_hospitalizaiton_for_a_room_that_doesn_t_exist() throws Exception {
        long id = 10000;
        HospitalizationRequest request = util.makeHospitalizationRequest();
        request.setHospitalRoomId(id);

        resultAction = mvc.perform(post("/hospitalization/hospitalize")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying the room doesn't exist")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_room_doesn_t_exist(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }



    @When("User tries to make a hospitalization for a referral that doesn't exist")
    public void user_tries_to_make_a_hospitalization_for_a_referral_that_doesn_t_exist() throws Exception {
        long id = 10000;
        HospitalizationRequest request = util.makeHospitalizationRequest();
        request.setReferralId(id);

        HospitalRoom hospitalRoom = hospitalRoomRepository.findById(request.getHospitalRoomId()).get();

        assertNotNull(hospitalRoom);

        resultAction = mvc.perform(post("/hospitalization/hospitalize")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying the referral doesn't exist")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_referral_doesn_t_exist(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }



    @When("User tries to make a hospitalization for a patient that is already hospitalized")
    public void user_tries_to_make_a_hospitalization_for_a_patient_that_is_already_hospitalized() throws Exception {
        HospitalizationRequest request = util.makeHospitalizationRequest();
        request.setLbp(UUID.fromString("c1c8ba08-966a-4cc5-b633-d1ef15d7caaf"));

        HospitalRoom hospitalRoom = hospitalRoomRepository.findById(request.getHospitalRoomId()).get();
        Patient patient = patientRepository.findByLbp(request.getLbp()).get();

        assertNotNull(hospitalRoom);
        assertNotNull(patient);

        boolean alreadyHospitalized = hospitalizationRepository.patientAlreadyHospitalized(patient);

        assertTrue(alreadyHospitalized);

        resultAction = mvc.perform(post("/hospitalization/hospitalize")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient is already hospitalized")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_is_already_hospitalized(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }



    @When("User tries to make a hospitalization for a patient")
    public void user_tries_to_make_a_hospitalization_for_a_patient() throws Exception {
        HospitalizationRequest request = util.makeHospitalizationRequest();

        Patient patient = patientRepository.findByLbp(request.getLbp()).get();
        HospitalRoom hospitalRoom = hospitalRoomRepository.findById(request.getHospitalRoomId()).get();

        assertNotNull(patient);
        assertNotNull(hospitalRoom);

        boolean alreadyHospitalized = hospitalizationRepository.patientAlreadyHospitalized(patient);

        assertFalse(alreadyHospitalized);

        resultAction = mvc.perform(post("/hospitalization/hospitalize")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("A new hospitalization is made and saved for that patient")
    public void a_new_hospitalization_is_made_and_saved_for_that_patient() throws Exception {
        HospitalizationRequest request = util.makeHospitalizationRequest();

        resultAction.andDo(MockMvcResultHandlers.print());

        Patient patient = patientRepository.findByLbp(request.getLbp()).get();
        HospitalRoom hospitalRoom = hospitalRoomRepository.findById(request.getHospitalRoomId()).get();
        Hospitalization hospitalization = hospitalizationRepository.findByHospitalRoomAndPatientAndDischargeDateIsNull(hospitalRoom, patient).get();

        assertNotNull(hospitalization);

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(hospitalization.getId()))
                .andExpect(jsonPath("$.lbp").value(patient.getLbp().toString()))
                .andExpect(jsonPath("$.hospitalRoomId").value(hospitalRoom.getId()));
    }
}
