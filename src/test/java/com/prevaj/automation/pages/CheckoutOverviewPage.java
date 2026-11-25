package com.prevaj.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.util.List;

public class CheckoutOverviewPage extends BasePage {

    private static final By ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By ITEM_QUANTITY = By.cssSelector("[data-test='item-quantity']");
    private static final By ITEM_TOTAL_LABEL = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX_LABEL = By.cssSelector("[data-test='tax-label']");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    public List<String> getItemNames() {
        waitUntilLoaded();
        return getTexts(ITEM_NAME);
    }

    public List<BigDecimal> getItemPrices() {
        waitUntilLoaded();
        return getTexts(ITEM_PRICE).stream().map(CheckoutOverviewPage::parseAmount).toList();
    }

    public List<Integer> getItemQuantities() {
        waitUntilLoaded();
        return getTexts(ITEM_QUANTITY).stream().map(Integer::parseInt).toList();
    }

    public BigDecimal getItemTotal() {
        return parseAmount(getText(ITEM_TOTAL_LABEL));
    }

    public BigDecimal getTax() {
        return parseAmount(getText(TAX_LABEL));
    }

    private void waitUntilLoaded() {
        waitForVisible(ITEM_TOTAL_LABEL);
    }

    private static BigDecimal parseAmount(String text) {
        return new BigDecimal(text.substring(text.indexOf('$') + 1).trim());
    }
}
