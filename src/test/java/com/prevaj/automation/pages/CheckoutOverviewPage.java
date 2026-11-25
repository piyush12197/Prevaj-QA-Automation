package com.prevaj.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public class CheckoutOverviewPage {

    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By ITEM_QUANTITY = By.cssSelector("[data-test='item-quantity']");
    private static final By ITEM_TOTAL_LABEL = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX_LABEL = By.cssSelector("[data-test='tax-label']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CheckoutOverviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public List<String> getItemNames() {
        waitUntilLoaded();
        return driver.findElements(ITEM_NAME).stream().map(WebElement::getText).toList();
    }

    public List<BigDecimal> getItemPrices() {
        waitUntilLoaded();
        return driver.findElements(ITEM_PRICE).stream().map(price -> parseAmount(price.getText())).toList();
    }

    public List<Integer> getItemQuantities() {
        waitUntilLoaded();
        return driver.findElements(ITEM_QUANTITY).stream().map(quantity -> Integer.parseInt(quantity.getText())).toList();
    }

    public BigDecimal getItemTotal() {
        return parseAmount(waitUntilLoaded().getText());
    }

    public BigDecimal getTax() {
        waitUntilLoaded();
        return parseAmount(driver.findElement(TAX_LABEL).getText());
    }

    private WebElement waitUntilLoaded() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ITEM_TOTAL_LABEL));
    }

    private static BigDecimal parseAmount(String text) {
        return new BigDecimal(text.substring(text.indexOf('$') + 1).trim());
    }
}
