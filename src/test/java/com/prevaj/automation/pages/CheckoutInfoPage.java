package com.prevaj.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CheckoutInfoPage {

    private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE_BUTTON = By.cssSelector("[data-test='continue']");

    private final WebDriverWait wait;

    public CheckoutInfoPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterFirstName(String firstName) {
        type(FIRST_NAME, firstName);
    }

    public void enterLastName(String lastName) {
        type(LAST_NAME, lastName);
    }

    public void enterPostalCode(String postalCode) {
        type(POSTAL_CODE, postalCode);
    }

    public void continueCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(CONTINUE_BUTTON)).click();
    }

    private void type(By field, String text) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(field)).sendKeys(text);
    }
}
