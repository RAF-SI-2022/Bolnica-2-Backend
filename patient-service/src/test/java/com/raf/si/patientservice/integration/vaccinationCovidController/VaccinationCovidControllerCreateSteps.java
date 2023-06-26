package com.raf.si.patientservice.integration.vaccinationCovidController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.request.VaccinationCovidRequest;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.localTypeDefAdapters.LocalDateTimeAdapter;
import com.raf.si.patientservice.model.AvailableTerm;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.model.ScheduledVaccinationCovid;
import com.raf.si.patientservice.model.enums.testing.Availability;
import com.raf.si.patientservice.repository.AvailableTermRepository;
import com.raf.si.patientservice.repository.ScheduledMedExamRepository;
import com.raf.si.patientservice.repository.ScheduledVaccinationCovidRepository;
import com.raf.si.patientservice.repository.VaccinationCovidRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VaccinationCovidControllerCreateSteps extends CucumberConfig {

    @Autowired
    private ScheduledVaccinationCovidRepository scheduledVaccinationCovidRepository;
    @Autowired
    private VaccinationCovidRepository vaccinationCovidRepository;
    @Autowired
    private AvailableTermRepository availableTermRepository;
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


    @When("nurse provides valid information for scheduled vaccination covid")
    public void nurse_provides_valid_information_for_scheduled_vaccination_covid() throws Exception {
        ScheduledVaccinationRequest request = util.makeSchedVaccinationCovidRequest();

        resultAction= mvc.perform(post("/vaccination/schedule/"+util.getPatientBootstrapLbp())
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("Created scheduled vaccination covid exam is returned")
    public void created_scheduled_vaccination_covid_exam_is_returned() throws Exception {
        List<ScheduledVaccinationCovid> createdRequest= scheduledVaccinationCovidRepository
                .findByPatient_lbp(util.getPatientBootstrapLbp()).get();
        assertNotNull(createdRequest);
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdRequest
                        .get(createdRequest.size()-1).getId()));
    }

    @When("Nurse tries to schedule new vaccination covid but given date is in past")
    public void nurse_tries_to_schedule_new_vaccination_covid_but_given_date_is_in_past() throws Exception {
        ScheduledVaccinationRequest request = util.makeSchedVaccinationCovidRequest();
        request.setDateAndTime(LocalDateTime.now().minusHours(3));

        resultAction= mvc.perform(post("/vaccination/schedule/"+util.getPatientBootstrapLbp())
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for given vaccination date")
    public void bad_request_exception_is_thrown_with_status_code_for_given_vaccination_date(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse tries to schedule new vaccination covid but patient has sched vacc for given date")
    public void nurse_tries_to_schedule_new_vaccination_covid_but_patient_has_sched_vacc_for_given_date() throws Exception {
        ScheduledVaccinationRequest request = util.makeSchedVaccinationCovidRequest();
        request.setDateAndTime(util.getPatientBootStrapSchedVaccinationDate().minusMinutes(45));

        resultAction= mvc.perform(post("/vaccination/schedule/"+util.getPatientWithSchedVacc())
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for given vacc date")
    public void bad_request_exception_is_thrown_with_status_code_for_given_vacc_date(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse tries to schedule new vaccination covid but given date is fully booked")
    public void nurse_tries_to_schedule_new_vaccination_covid_but_given_date_is_fully_booked() throws Exception {
        ScheduledVaccinationRequest request = util.makeSchedVaccinationCovidRequest();

        AvailableTerm availableTerm = availableTermRepository.findById(1L).get();
        availableTerm.setAvailability(Availability.POTPUNO_POPUNJEN_TERMIN);
        availableTermRepository.save(availableTerm);

        request.setDateAndTime(availableTerm.getDateAndTime());

        resultAction= mvc.perform(post("/vaccination/schedule/"+util.getPatientBootstrapLbp())
                .header("Authorization", "Bearer " + util.generateNurseTokenForValidPbo())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for given vaccination date booked")
    public void bad_request_exception_is_thrown_with_status_code_for_given_vaccination_date_booked(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse tries to create vaccination covid but date is in the future")
    public void nurse_tries_to_create_vaccination_covid_but_date_is_in_the_future() throws Exception {
        VaccinationCovidRequest request = util.makeVaccinationCovidRequest();
        request.setDateTime(LocalDateTime.now().plusDays(2));

        resultAction= mvc.perform(post("/vaccination/create/"+util.getPatientBootstrapLbp())
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("BadRequestException is thrown with status code {int} for given vaccination covid date")
    public void bad_request_exception_is_thrown_with_status_code_for_given_vaccination_covid_date(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse tries to create vaccination covid but given vaccine name is unknown")
    public void nurse_tries_to_create_vaccination_covid_but_given_vaccine_name_is_unknown() throws Exception {
        VaccinationCovidRequest request = util.makeVaccinationCovidRequest();
        request.setVaccineName("JunJul");

        resultAction= mvc.perform(post("/vaccination/create/"+util.getPatientBootstrapLbp())
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for given vaccine name")
    public void bad_request_exception_is_thrown_with_status_code_for_given_vaccine_name(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse tries to create vaccination covid but given vaccination id does not exits")
    public void nurse_tries_to_create_vaccination_covid_but_given_vaccination_id_does_not_exits() throws Exception {
        VaccinationCovidRequest request = util.makeVaccinationCovidRequest();
        request.setVaccinationId(10L);

        resultAction= mvc.perform(post("/vaccination/create/"+util.getPatientBootstrapLbp())
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("BadRequestException is thrown with status code {int} for given vaccination id")
    public void bad_request_exception_is_thrown_with_status_code_for_given_vaccination_id(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse provides valid information for vaccination covid")
    public void nurse_provides_valid_information_for_vaccination_covid() throws Exception {
        VaccinationCovidRequest request = util.makeVaccinationCovidRequest();

        resultAction= mvc.perform(post("/vaccination/create/"+util.getPatientBootstrapLbp())
                .header("Authorization", "Bearer " + util.generateNurseTokenValid())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("Created vaccination covid exam is returned")
    public void created_vaccination_covid_exam_is_returned() throws Exception {
        resultAction.andExpect(status().isOk());
    }








}
