package com.prevaj.automation.pages;

import com.prevaj.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.cssSelector("[data-test='username']");
    private static final By PASSWORD = By.cssSelector("[data-test='password']");
    private static final By LOGIN_BUTTON = By.cssSelector("[data-test='login-button']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(ConfigReader.sauceDemoBaseUrl());
    }

    public void logIn(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
    }
}
