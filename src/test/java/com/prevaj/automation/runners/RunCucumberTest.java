package com.prevaj.automation.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * The single entry point for the Cucumber scenarios. The reports are configured on this suite only,
 * so each run writes them once.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.prevaj.automation.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "html:build/reports/cucumber/cucumber.html, json:build/reports/cucumber/cucumber.json")
public class RunCucumberTest {
}
