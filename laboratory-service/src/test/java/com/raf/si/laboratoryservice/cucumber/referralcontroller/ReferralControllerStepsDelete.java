package com.raf.si.laboratoryservice.cucumber.referralcontroller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.laboratoryservice.cucumber.CucumberConfig;
import com.raf.si.laboratoryservice.cucumber.UtilsHelper;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.utils.JwtUtil;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

public class ReferralControllerStepsDelete extends CucumberConfig {
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
}
