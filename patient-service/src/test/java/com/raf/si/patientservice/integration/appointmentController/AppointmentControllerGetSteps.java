package com.raf.si.patientservice.integration.appointmentController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.Appointment;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.AppointmentRepository;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AppointmentControllerGetSteps extends CucumberConfig {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    private Gson gson;
    private ResultActions resultAction;

    private UUID randomLbz = UUID.randomUUID();


    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }


    @When("User tries to get all appointments for the pbo and it is successful")
    public void user_tries_to_get_all_appointments_for_the_pbo_and_it_is_successful() throws Exception {
        resultAction = mvc.perform(get("/appointment")
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("A page of appointments is returned")
    public void a_page_of_appointments_is_returned() throws Exception {
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }




    @Given("There is an appointment with connected lbz, but the user with that lbz doesn't exist")
    public void there_is_an_appointment_with_connected_lbz_but_the_user_with_that_lbz_doesn_t_exist() throws Exception {
        Optional<Patient> patientOptional = patientRepository.findByLbp(util.getPatientBootstrapLbp());

        assertTrue(patientOptional.isPresent());

        Appointment appointment = util.makeAppointment();
        appointment.setPatient(patientOptional.get());

        appointmentRepository.save(appointment);
    }
    @When("User tries to get all appointments for the pbo, but some users connected to the appointments don't exist")
    public void user_tries_to_get_all_appointments_for_the_pbo_but_some_users_connected_to_the_appointments_don_t_exist() throws Exception {
        resultAction = mvc.perform(get("/appointment")
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("NotFoundException is thrown with status code {int} saying the lbz doesn't exist")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_lbz_doesn_t_exist(Integer statusCode) throws Exception {
        resultAction.andDo(MockMvcResultHandlers.print());
        resultAction.andExpect(status().is(statusCode));
    }
}
