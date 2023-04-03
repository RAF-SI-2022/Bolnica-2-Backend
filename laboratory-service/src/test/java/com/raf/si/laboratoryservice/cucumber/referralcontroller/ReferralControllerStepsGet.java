package com.raf.si.laboratoryservice.cucumber.referralcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.laboratoryservice.cucumber.CucumberConfig;
import com.raf.si.laboratoryservice.cucumber.UtilsHelper;
import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReferralControllerStepsGet extends CucumberConfig {
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

    @When("given referral does not exist for given id")
    public void given_referral_does_not_exist_for_given_id() throws Exception {
        resultActions = mvc.perform(get("/referral/2")
                .header("Authorization", "Bearer " + util.getToken())
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("NotFoundException is thrown with status code {int} for given id")
    public void not_found_exception_is_thrown_with_status_code_for_given_id (Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("given referral exists for given id")
    public void given_referral_exists_for_given_id() throws Exception {
        resultActions = mvc.perform(get("/referral/1")
                .header("Authorization", "Bearer " + util.getToken())
                .contentType(MediaType.APPLICATION_JSON));
    }
    @Then("referral is returned for given id")
    public void referral_is_returned_for_given_id() throws Exception {
        Referral referral = referralRepository.findById(1L).orElse(null);
        assertNotNull(referral);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.referralDiagnosis").value(referral.getReferralDiagnosis()));
    }
}
