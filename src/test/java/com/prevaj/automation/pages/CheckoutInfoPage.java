package com.prevaj.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutInfoPage extends BasePage {

    private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE_BUTTON = By.cssSelector("[data-test='continue']");

    public CheckoutInfoPage(WebDriver driver) {
        super(driver);
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
        click(CONTINUE_BUTTON);
    }
}
