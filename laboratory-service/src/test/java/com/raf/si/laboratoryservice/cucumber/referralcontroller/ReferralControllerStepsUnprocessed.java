package com.raf.si.laboratoryservice.cucumber.referralcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.laboratoryservice.cucumber.CucumberConfig;
import com.raf.si.laboratoryservice.cucumber.UtilsHelper;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReferralControllerStepsUnprocessed extends CucumberConfig {
    @Autowired
    private ReferralRepository referralRepository;
    private Gson gson;
    private UtilsHelper util;
    private ResultActions resultActions;

    @Autowired
    private JwtUtil jwtUtil;

    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

    @When("doctor provides invalid information for fetching unprocessed referrals")
    public void doctor_provides_invalid_information_for_fetching_unprocessed_referrals() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lbp", "d79f77be-0a0e-4e2f-88a5-5f5d5cdd1e2c");

        resultActions = mvc.perform(get("/referral/unprocessed").queryParams(queryParams)
                .header("Authorization", "Bearer " + util.generateToken())
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int}")
    public void not_found_exception_is_thrown_with_status_code(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("doctor provides valid information for fetching unprocessed referrals")
    public void doctor_provides_valid_information_for_fetching_unprocessed_referrals() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lbp", "c208f04d-9551-404e-8c54-9321f3ae9be8");

        resultActions = mvc.perform(get("/referral/unprocessed").queryParams(queryParams)
                .header("Authorization", "Bearer " + util.generateToken())
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("return the list of unprocessed referrals")
    public void return_the_list_of_unprocessed_referrals() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


}