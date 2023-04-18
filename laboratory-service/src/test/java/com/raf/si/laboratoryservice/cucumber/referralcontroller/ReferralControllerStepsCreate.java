package com.raf.si.laboratoryservice.cucumber.referralcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.laboratoryservice.cucumber.CucumberConfig;
import com.raf.si.laboratoryservice.cucumber.UtilsHelper;
import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReferralControllerStepsCreate extends CucumberConfig {
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

    @When("doctor provides invalid information for creating a referral")
    public void doctor_provides_invalid_information_for_creating_a_referral() throws Exception {
        CreateReferralRequest createReferralRequest = new CreateReferralRequest();
        resultActions = mvc.perform(post("/referral/create")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(createReferralRequest))
                .contentType(MediaType.APPLICATION_JSON));

    }
    @Then("BadRequestException is thrown with status code {int}")
    public void bad_request_exception_is_thrown_with_status_code(Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @When("doctor provides valid information for creating a referral")
    public void doctor_provides_valid_information_for_creating_a_referral() throws Exception {
        CreateReferralRequest createReferralRequest = util.createReferralRequest();

        resultActions = mvc.perform(post("/referral/create")
                .header("Authorization", "Bearer " + util.generateToken())
                .content(gson.toJson(createReferralRequest))
                .contentType(MediaType.APPLICATION_JSON));

    }

    @Then("created referral is returned")
    public void created_referral_is_returned() throws Exception {
        Referral referral = referralRepository.findById(2L).orElse(null);
        assertNotNull(referral);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.referralDiagnosis").value(referral.getReferralDiagnosis()));
    }
}
