package com.raf.si.patientservice.integration.appointmentController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.Appointment;
import com.raf.si.patientservice.model.enums.appointment.AppointmentStatus;
import com.raf.si.patientservice.repository.AppointmentRepository;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AppointmentControllerChangeStatusSteps extends CucumberConfig {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
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

    @When("User tries to change appointment status, but the appointment doesn't exist")
    public void user_tries_to_change_appointment_status_but_the_appointment_doesn_t_exist() throws Exception {
        long id = 10000;
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);

        assertTrue(appointmentOptional.isEmpty());

        String route = String.format("/appointment/change-status/%d?status=%s", id, "");
        resultAction = mvc.perform(put(route)
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("BadRequestException is thrown with status code {int} saying the appointment doesn't exist")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_appointment_doesn_t_exist(Integer statusCode) throws Exception {
        resultAction.andDo(MockMvcResultHandlers.print());
        resultAction.andExpect(status().is(statusCode));
    }




    @When("User tries to change appointment status, but the appointment status doesn't exist")
    public void user_tries_to_change_appointment_status_but_the_appointment_status_doesn_t_exist() throws Exception {
        long id = 1;
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);

        assertTrue(appointmentOptional.isPresent());

        String route = String.format("/appointment/change-status/%d?status=%s", id, "Nema");
        resultAction = mvc.perform(put(route)
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("BadRequestException is thrown with status code {int} saying the appointment status doesn't exist")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_appointment_status_doesn_t_exist(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }




    @When("User tries to change appointment status and it is successful")
    public void user_tries_to_change_appointment_status_and_it_is_successful() throws Exception {
        long id = 1;
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(id);

        assertTrue(appointmentOptional.isPresent());

        String status = "Otkazan";
        AppointmentStatus appointmentStatus = AppointmentStatus.valueOfNotation(status);

        assertNotNull(appointmentStatus);

        String route = String.format("/appointment/change-status/%d?status=%s", id, status);
        resultAction = mvc.perform(put(route)
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("Appointment status for that appointment is changed and saved in the database")
    public void appointment_status_for_that_appointment_is_changed_and_saved_in_the_database() throws Exception {
        long id = 1;
        Appointment appointment = appointmentRepository.findById(id).get();

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status.notation").value(appointment.getStatus().getNotation()));
    }
}
