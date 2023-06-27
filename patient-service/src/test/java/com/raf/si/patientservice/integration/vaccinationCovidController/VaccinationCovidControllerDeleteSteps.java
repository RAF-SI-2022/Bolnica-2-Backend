package com.raf.si.patientservice.integration.vaccinationCovidController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.localTypeDefAdapters.LocalDateTimeAdapter;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledVaccinationCovid;
import com.raf.si.patientservice.repository.AvailableTermRepository;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.repository.ScheduledVaccinationCovidRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VaccinationCovidControllerDeleteSteps extends CucumberConfig {

    @Autowired
    private ScheduledVaccinationCovidRepository scheduledVaccinationCovidRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private AvailableTermRepository availableTermRepository;
    @Autowired
    private Gson gson;
    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    private ResultActions resultAction;
    private Long id;

    @Before
    public  void  init(){
        util= new UtilsHelper(jwtUtil);
        gson= new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }


    @When("Nurse tries to delete scheduled vaccination covid but given id does not exits")
    public void nurse_tries_to_delete_scheduled_vaccination_covid_but_given_id_does_not_exits() throws Exception {
        resultAction= mvc.perform(delete(String.format("/vaccination/scheduled/delete/%s", 100L))
                .header("Authorization", "Bearer " + util.generateNurseToken()));
    }
    @Then("NotFoundException is thrown with status code {int} for given scheduled vaccination covid id")
    public void not_found_exception_is_thrown_with_status_code_for_given_scheduled_vaccination_covid_id(Integer statusCode) throws Exception {
        resultAction.andExpect(status().is(statusCode));
    }

    @When("Nurse provides valid information to delete scheduled vaccination covid")
    public void nurse_provides_valid_information_to_delete_scheduled_vaccination_covid() throws Exception {
        Patient patient = patientRepository.findByLbp(util.getPatientBootstrapLbp()).orElse(null);
        assertNotNull(patient);
        ScheduledVaccinationCovid scheduledVaccinationCovid = util.makeScheduledVaccinationCovid(patient);

        availableTermRepository.save(scheduledVaccinationCovid.getAvailableTerm());
        ScheduledVaccinationCovid saved = scheduledVaccinationCovidRepository.save(scheduledVaccinationCovid);
        id = saved.getId();

        resultAction= mvc.perform(delete(String.format("/vaccination/scheduled/delete/%s", id))
                .header("Authorization", "Bearer " + util.generateNurseToken()));
    }
    @Then("Scheduled vaccination covid gets deleted")
    public void scheduled_vaccination_covid_gets_deleted() throws Exception {
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }



}
