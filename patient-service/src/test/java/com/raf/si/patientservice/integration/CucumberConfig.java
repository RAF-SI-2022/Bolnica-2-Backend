package com.raf.si.patientservice.integration;


import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
public class CucumberConfig {

    @Autowired
    protected MockMvc mvc;
}
