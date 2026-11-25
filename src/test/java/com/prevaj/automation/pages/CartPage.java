package com.prevaj.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {

    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_QUANTITY = By.cssSelector("[data-test='item-quantity']");
    private static final By CHECKOUT_BUTTON = By.cssSelector("[data-test='checkout']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        click(CART_LINK);
    }

    public int getBadgeCount() {
        List<WebElement> badge = driver.findElements(CART_BADGE);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.getFirst().getText());
    }

    public List<String> getItemNames() {
        waitUntilLoaded();
        return getTexts(ITEM_NAME);
    }

    public List<Integer> getItemQuantities() {
        waitUntilLoaded();
        return getTexts(ITEM_QUANTITY).stream().map(Integer::parseInt).toList();
    }

    public void remove(String productName) {
        By removeButton = By.xpath("//*[@data-test='inventory-item'][.//*[@data-test='inventory-item-name'][normalize-space()='"
                + productName + "']]//button[starts-with(@data-test, 'remove')]");
        click(removeButton);
        waitForInvisible(removeButton);
    }

    public void checkout() {
        click(CHECKOUT_BUTTON);
    }

    private void waitUntilLoaded() {
        waitForVisible(CHECKOUT_BUTTON);
    }
}
