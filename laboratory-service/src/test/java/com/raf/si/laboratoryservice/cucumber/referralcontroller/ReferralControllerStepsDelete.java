package com.raf.si.laboratoryservice.cucumber.referralcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.laboratoryservice.cucumber.CucumberConfig;
import com.raf.si.laboratoryservice.cucumber.UtilsHelper;
import com.raf.si.laboratoryservice.dto.request.UpdateLabExamStatusRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.repository.LabWorkOrderRepository;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.utils.JwtUtil;
import com.raf.si.laboratoryservice.utils.TokenPayload;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReferralControllerStepsDelete extends CucumberConfig {
    @Autowired
    private ReferralRepository referralRepository;
    private Gson gson;
    private UtilsHelper util;
    private ResultActions resultActions;

    @Autowired
    private JwtUtil jwtUtil;
    private Referral referral;

    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

    }

    @When("doctor provides invalid information for referral deletion")
    public void doctor_provides_invalid_information_for_referral_deletion() throws Exception {
        resultActions = mvc.perform(delete("/referral/delete/10")
                .header("Authorization", "Bearer " + util.generateToken())
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("NotFoundException is thrown with status code {int} for deletion")
    public void not_found_exception_is_thrown_with_status_code_for_given_id_for_deletion (Integer statusCode) throws Exception {
        resultActions.andExpect(status().is(statusCode));
    }

    @Given("a referral with ID {int} exists")
    public void a_referral_with_id_exists(Integer id) throws Exception {
        resultActions = mvc.perform(get(String.format("/referral/delete/%s", id))
                .header("Authorization", "Bearer " + util.generateToken())
                .contentType(MediaType.APPLICATION_JSON));
   }

    @Given("no lab work order exists for the referral")
    public void no_lab_work_order_exists_for_the_referral() {
        referral = referralRepository.findById(1L).get();
        referral.setLabWorkOrder(null);
        referralRepository.save(referral);
    }

    @When("doctor deletes the referral with ID {int}")
    public void doctor_deletes_the_referral_with_id(Integer id) throws Exception {
        resultActions = mvc.perform(delete(String.format("/referral/delete/%s", id))
        .header("Authorization", "Bearer " + util.generateToken())
        .contentType(MediaType.APPLICATION_JSON));
    }

    @Then("return the deleted referral")
    public void return_the_deleted_referral() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));
    }
}
