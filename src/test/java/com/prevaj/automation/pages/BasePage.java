package com.prevaj.automation.pages;

import com.prevaj.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Objects;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver");
        this.wait = new WebDriverWait(driver, ConfigReader.explicitWait());
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        waitForVisible(locator).sendKeys(text);
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    /**
     * Texts of the matching elements currently on the page, in page order. Does not wait.
     */
    protected List<String> getTexts(By locator) {
        return driver.findElements(locator).stream().map(WebElement::getText).toList();
    }
}
