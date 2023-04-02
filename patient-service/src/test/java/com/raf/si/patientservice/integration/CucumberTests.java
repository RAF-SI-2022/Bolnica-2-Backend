package com.raf.si.patientservice.integration;


import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static  io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines({"cucumber"})
@SelectClasspathResource("features/schedMedExamController")
@ConfigurationParameter(key= GLUE_PROPERTY_NAME, value = "com.raf.si.patientservice.integration")
public class CucumberTests {
}
