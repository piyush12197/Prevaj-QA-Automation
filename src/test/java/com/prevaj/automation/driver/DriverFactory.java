package com.prevaj.automation.driver;

import com.prevaj.automation.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Map;

/**
 * Creates a new browser session from the configured browser and headless mode.
 * Selenium Manager resolves the driver binaries, and each session uses its own temporary profile.
 * The caller owns the returned driver and must quit it.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        boolean headless = ConfigReader.headless();
        return switch (ConfigReader.browser()) {
            case CHROME -> new ChromeDriver(chromeOptions(headless));
            case FIREFOX -> new FirefoxDriver(firefoxOptions(headless));
        };
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        // Chrome's breached-password dialog blocks native clicks after logging in to SauceDemo.
        // The preference is set only in the temporary profile of this session.
        options.setExperimentalOption("prefs", Map.of("profile.password_manager_leak_detection", false));
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }
}
