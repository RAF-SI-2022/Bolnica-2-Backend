package com.raf.si.patientservice.integration.hospitalRoomController;

import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HospitalRoomControllerGetSteps extends CucumberConfig {

    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    private ResultActions resultAction;

    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
    }

    @When("User tries to get all hospital rooms available")
    public void user_tries_to_get_all_hospital_rooms_available() throws Exception {
        UUID pbo = UUID.fromString("c0979e25-2bb1-4582-87a9-aa175777a65d");
        resultAction = mvc.perform(get(String.format("/hospital-room/?pbo=%s", pbo))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("Page of hospital rooms is returned")
    public void page_of_hospital_rooms_is_returned() throws Exception {
        resultAction.andDo(MockMvcResultHandlers.print());
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @When("User tries to get information for all rooms in the department")
    public void user_tries_to_get_information_for_all_rooms_in_the_department() throws Exception {
        UUID pbo = UUID.fromString("c0979e25-2bb1-4582-87a9-aa175777a65d");
        resultAction = mvc.perform(get(String.format("/hospital-room/beds?pbo=%s", pbo))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("Number of total, available and occupied rooms is returned")
    public void number_of_total_available_and_occupied_rooms_is_returned() throws Exception {
        resultAction.andDo(MockMvcResultHandlers.print());
        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBeds").value(15))
                .andExpect(jsonPath("$.bedsInUse").value(1))
                .andExpect(jsonPath("$.availableBeds").value(14));
    }
}
