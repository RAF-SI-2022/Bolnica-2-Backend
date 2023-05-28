package com.raf.si.patientservice.integration.appointmentController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.CreateAppointmentRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.Appointment;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.AppointmentRepository;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AppointmentControllerCreateSteps extends CucumberConfig {

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

    @When("User tries to create an appointment for a patient, but the patient has an appointment that day")
    public void user_tries_to_create_an_appointment_for_a_patient_but_the_patient_has_an_appointment_that_day() throws Exception {
        CreateAppointmentRequest request = util.makeCreateAppointmentRequest();
        request.setReceiptDate(new Date());
        request.setLbp(UUID.fromString("c1c8ba08-966a-4cc5-b633-d1ef15d7caaf"));

        Patient patient = patientRepository.findByLbp(request.getLbp()).get();

        assertNotNull(patient);

        Date startDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date endDate = DateUtils.addDays(startDate, 1);
        boolean hasAppointment = appointmentRepository.patientHasAppointmentDateBetween(patient, startDate, endDate);

        assertTrue(hasAppointment);

        resultAction = mvc.perform(post("/appointment/create")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient has an appointment that day")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_has_an_appointment_that_day(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }




    @When("User tries to create an appointment for a patient, but the employee doesn't exist")
    public void user_tries_to_create_an_appointment_for_a_patient_but_the_employee_doesn_t_exist() throws Exception {
        CreateAppointmentRequest request = util.makeCreateAppointmentRequest();

        Patient patient = patientRepository.findByLbp(request.getLbp()).get();

        assertNotNull(patient);

        Date startDate = DateUtils.truncate(request.getReceiptDate(), Calendar.DAY_OF_MONTH);
        Date endDate = DateUtils.addDays(startDate, 1);
        boolean hasAppointment = appointmentRepository.patientHasAppointmentDateBetween(patient, startDate, endDate);

        assertFalse(hasAppointment);

        resultAction = mvc.perform(post("/appointment/create")
                .header("Authorization", "Bearer " + util.generateTokenEmployeeDoesntExist())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying the token is invalid because the employee doesn't exist")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_token_is_invalid_because_the_employee_doesn_t_exist(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }




    @When("User tries to create an appointment for a patient, but the department doesn't exist")
    public void user_tries_to_create_an_appointment_for_a_patient_but_the_department_doesn_t_exist() throws Exception {
        CreateAppointmentRequest request = util.makeCreateAppointmentRequest();

        Patient patient = patientRepository.findByLbp(request.getLbp()).get();

        assertNotNull(patient);

        Date startDate = DateUtils.truncate(request.getReceiptDate(), Calendar.DAY_OF_MONTH);
        Date endDate = DateUtils.addDays(startDate, 1);
        boolean hasAppointment = appointmentRepository.patientHasAppointmentDateBetween(patient, startDate, endDate);

        assertFalse(hasAppointment);

        resultAction = mvc.perform(post("/appointment/create")
                .header("Authorization", "Bearer " + util.generateTokenDepartmentDoesntExist())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} saying the department doesn't exist")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_department_doesn_t_exist(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }




    @When("User tries to create an appointment for a patient and it is successful")
    public void user_tries_to_create_an_appointment_for_a_patient_and_it_is_successful() throws Exception {
        CreateAppointmentRequest request = util.makeCreateAppointmentRequest();

        Patient patient = patientRepository.findByLbp(request.getLbp()).get();

        assertNotNull(patient);

        Date startDate = DateUtils.truncate(request.getReceiptDate(), Calendar.DAY_OF_MONTH);
        Date endDate = DateUtils.addDays(startDate, 1);
        boolean hasAppointment = appointmentRepository.patientHasAppointmentDateBetween(patient, startDate, endDate);

        assertFalse(hasAppointment);

        resultAction = mvc.perform(post("/appointment/create")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("Appointment for that patient is made and saved in the database")
    public void appointment_for_that_patient_is_made_and_saved_in_the_database() throws Exception {
        resultAction.andDo(MockMvcResultHandlers.print());

        CreateAppointmentRequest request = util.makeCreateAppointmentRequest();

        Patient patient = patientRepository.findByLbp(request.getLbp()).get();
        UUID pbo = util.getBootstrapPbo();
        Date startDate = DateUtils.addDays(request.getReceiptDate(), -1);
        Date endDate = DateUtils.addDays(request.getReceiptDate(), 1);

        Appointment appointment = appointmentRepository.findByPatientAndPboAndReceiptDateBetween(patient, pbo, startDate, endDate).get();

        assertNotNull(appointment);

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointment.getId()))
                .andExpect(jsonPath("$.patient.lbp").value(patient.getLbp().toString()))
                .andExpect(jsonPath("$.department.pbo").value(pbo.toString()));
    }
}
