package com.raf.si.patientservice.integration.vaccinationCovidController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.localTypeDefAdapters.LocalDateTimeAdapter;
import com.raf.si.patientservice.repository.AvailableTermRepository;
import com.raf.si.patientservice.repository.ScheduledVaccinationCovidRepository;
import com.raf.si.patientservice.repository.VaccinationCovidRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VaccinationCovidControllerGetSteps extends CucumberConfig {

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


    @When("Nurse provides valid information for getting scheduled vaccination covid")
    public void nurse_provides_valid_information_for_getting_scheduled_vaccination_covid() throws Exception {
        resultAction= mvc.perform(get("/vaccination/scheduled")
                .header("Authorization", "Bearer " + util.generateNurseTokenForValidPbo()));
    }
    @Then("Nurse gets list of scheduled vaccination covid")
    public void nurse_gets_list_of_scheduled_vaccination_covid() throws Exception {
        resultAction.andExpect(status().isOk());
    }

    @When("Nurse provides valid information for dosage received covid")
    public void nurse_provides_valid_information_for_dosage_received_covid() throws Exception {
        resultAction= mvc.perform(get("/vaccination/received-dosage/"+util.getPatientBootstrapLbp())
                .header("Authorization", "Bearer " + util.generateNurseTokenValid()));
    }
    @Then("Gets dosage received for given patient lbp")
    public void gets_dosage_received_for_given_patient_lbp()throws Exception {
        resultAction.andExpect(status().isOk());
    }




}
