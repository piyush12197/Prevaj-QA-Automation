package com.prevaj.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class InventoryPage {

    private final WebDriverWait wait;

    public InventoryPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void addToCart(String productName) {
        wait.until(ExpectedConditions.elementToBeClickable(productButton(productName, "add-to-cart"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(productButton(productName, "remove")));
    }

    private static By productButton(String productName, String buttonPrefix) {
        return By.xpath("//*[@data-test='inventory-item'][.//*[@data-test='inventory-item-name'][normalize-space()='"
                + productName + "']]//button[starts-with(@data-test, '" + buttonPrefix + "')]");
    }
}
