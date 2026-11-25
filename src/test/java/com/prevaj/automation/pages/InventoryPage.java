package com.prevaj.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public void addToCart(String productName) {
        click(productButton(productName, "add-to-cart"));
        waitForVisible(productButton(productName, "remove"));
    }

    private static By productButton(String productName, String buttonPrefix) {
        return By.xpath("//*[@data-test='inventory-item'][.//*[@data-test='inventory-item-name'][normalize-space()='"
                + productName + "']]//button[starts-with(@data-test, '" + buttonPrefix + "')]");
    }
}
