package com.raf.si.laboratoryservice.cucumber.referralcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.laboratoryservice.cucumber.CucumberConfig;
import com.raf.si.laboratoryservice.cucumber.UtilsHelper;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReferralControllerStepsHistory extends CucumberConfig {
    @Autowired
    private ReferralRepository referralRepository;

    private Gson gson;
    private UtilsHelper util;
    private ResultActions resultActions;

    @Before
    public void initialization() {
        util = new UtilsHelper();
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

    @When("doctor provides valid information for referral history")
    public void doctor_provides_valid_information_for_referral_history() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lbp", "c208f04d-9551-404e-8c54-9321f3ae9be8");
        queryParams.add("dateFrom", "2023-04-03 00:00:00");
        queryParams.add("dateTo", "2023-04-07 00:00:00");
        queryParams.add("page", "0");
        queryParams.add("size", "10");

        resultActions = mvc.perform(get("/referral/history").queryParams(queryParams)
                .header("Authorization", "Bearer " + util.getToken())
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("page with given parameters is returned containing referral history")
    public void page_with_given_parameters_is_returned_containing_referral_history() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.referrals", hasSize(1)));
    }

    @When("doctor provides invalid information for referral history")
    public void doctor_provides_invalid_information_for_referral_history() throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("lbp", "c208f04d-9551-404e-8c54-9321f3ae9be8");
        queryParams.add("dateFrom", "2023-04-08 00:00:00");
        queryParams.add("dateTo", "2023-04-07 00:00:00");
        queryParams.add("page", "1");

        resultActions = mvc.perform(get("/referral/history").queryParams(queryParams)
                .header("Authorization", "Bearer " + util.getToken())
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("BadRequestException is thrown with status code {int} for referral history")
    public void bad_request_exception_is_thrown_with_status_code_for_referral_history(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }
}
