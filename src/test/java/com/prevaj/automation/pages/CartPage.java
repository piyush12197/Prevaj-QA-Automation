package com.prevaj.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage {

    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_QUANTITY = By.cssSelector("[data-test='item-quantity']");
    private static final By CHECKOUT_BUTTON = By.cssSelector("[data-test='checkout']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        wait.until(ExpectedConditions.elementToBeClickable(CART_LINK)).click();
    }

    public int getBadgeCount() {
        List<WebElement> badge = driver.findElements(CART_BADGE);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.getFirst().getText());
    }

    public List<String> getItemNames() {
        waitUntilLoaded();
        return driver.findElements(ITEM_NAME).stream().map(WebElement::getText).toList();
    }

    public List<Integer> getItemQuantities() {
        waitUntilLoaded();
        return driver.findElements(ITEM_QUANTITY).stream().map(quantity -> Integer.parseInt(quantity.getText())).toList();
    }

    public void remove(String productName) {
        By removeButton = By.xpath("//*[@data-test='inventory-item'][.//*[@data-test='inventory-item-name'][normalize-space()='"
                + productName + "']]//button[starts-with(@data-test, 'remove')]");
        wait.until(ExpectedConditions.elementToBeClickable(removeButton)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(removeButton));
    }

    public void checkout() {
        wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT_BUTTON)).click();
    }

    private void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(CHECKOUT_BUTTON));
    }
}
