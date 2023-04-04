package com.raf.si.patientservice.integration;


import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@IncludeEngines({"cucumber"})
@SelectClasspathResource("features")
@ConfigurationParameter(key= GLUE_PROPERTY_NAME, value = "com.raf.si.patientservice.integration")
public class CucumberTests {
}
