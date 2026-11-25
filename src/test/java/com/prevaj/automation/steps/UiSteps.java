package com.prevaj.automation.steps;

import com.prevaj.automation.pages.CartPage;
import com.prevaj.automation.pages.CheckoutInfoPage;
import com.prevaj.automation.pages.CheckoutOverviewPage;
import com.prevaj.automation.pages.InventoryPage;
import com.prevaj.automation.pages.LoginPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class UiSteps {

    private final List<String> addedItems = new ArrayList<>();
    private final List<String> removedItems = new ArrayList<>();
    private WebDriver driver;
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutInfoPage checkoutInfoPage;
    private CheckoutOverviewPage checkoutOverviewPage;

    @Before("@ui")
    public void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", Map.of("profile.password_manager_leak_detection", false));
        driver = new ChromeDriver(options);
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver);
        checkoutInfoPage = new CheckoutInfoPage(driver);
        checkoutOverviewPage = new CheckoutOverviewPage(driver);
    }

    @After("@ui")
    public void quitBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Given("I am on the home page")
    public void openHomePage() {
        loginPage.open();
    }

    @And("I log in with the following details")
    public void logInWithDetails(DataTable credentials) {
        Map<String, String> row = credentials.asMaps().getFirst();
        loginPage.logIn(row.get("userName"), row.get("Password"));
    }

    @And("I add the following items to the basket")
    public void addItemsToBasket(List<String> productNames) {
        productNames.forEach(inventoryPage::addToCart);
        addedItems.addAll(productNames);
    }

    @And("I should see {int} items added to the shopping cart")
    public void shouldSeeItemsAddedToShoppingCart(int itemCount) {
        assertThat(cartPage.getBadgeCount(), is(itemCount));
    }

    @And("I click on the shopping cart")
    public void clickOnShoppingCart() {
        cartPage.open();
    }

    @And("I verify that the QTY count for each item is {int}")
    public void verifyQuantityForEachItem(int expectedQuantity) {
        List<Integer> quantities = cartPage.getItemQuantities();
        assertThat(cartPage.getItemNames(), containsInAnyOrder(addedItems.toArray(String[]::new)));
        assertThat(quantities, hasSize(addedItems.size()));
        assertThat(quantities, everyItem(is(expectedQuantity)));
    }

    @And("I remove the following item")
    public void removeItems(List<String> productNames) {
        productNames.forEach(cartPage::remove);
        removedItems.addAll(productNames);
    }

    @And("I should see {int} items in the shopping cart")
    public void shouldSeeItemsInShoppingCart(int itemCount) {
        List<String> cartItems = cartPage.getItemNames();
        assertThat(cartPage.getBadgeCount(), is(itemCount));
        assertThat(cartItems, hasSize(itemCount));
        assertThat(cartItems, containsInAnyOrder(remainingItems()));
    }

    @And("I click on the CHECKOUT button")
    public void clickOnCheckoutButton() {
        cartPage.checkout();
    }

    @And("I type {string} for First Name")
    public void typeFirstName(String firstName) {
        checkoutInfoPage.enterFirstName(firstName);
    }

    @And("I type {string} for Last Name")
    public void typeLastName(String lastName) {
        checkoutInfoPage.enterLastName(lastName);
    }

    @And("I type {string} for ZIP\\/Postal Code")
    public void typePostalCode(String postalCode) {
        checkoutInfoPage.enterPostalCode(postalCode);
    }

    @When("I click on the CONTINUE button")
    public void clickOnContinueButton() {
        checkoutInfoPage.continueCheckout();
    }

    @Then("the item total should equal the total of the item prices on the list")
    public void itemTotalShouldEqualTotalOfItemPrices() {
        assertThat(checkoutOverviewPage.getItemNames(), containsInAnyOrder(remainingItems()));
        assertThat(checkoutOverviewPage.getItemTotal(), comparesEqualTo(calculateItemTotal()));
    }

    @And("a tax rate of {int}% should be applied to the item total")
    public void taxRateShouldBeAppliedToItemTotal(int taxRatePercent) {
        BigDecimal expectedTax = calculateItemTotal()
                .multiply(BigDecimal.valueOf(taxRatePercent).movePointLeft(2))
                .setScale(2, RoundingMode.HALF_UP);
        assertThat(checkoutOverviewPage.getTax(), comparesEqualTo(expectedTax));
    }

    private BigDecimal calculateItemTotal() {
        List<BigDecimal> prices = checkoutOverviewPage.getItemPrices();
        List<Integer> quantities = checkoutOverviewPage.getItemQuantities();
        assertThat(prices, is(not(empty())));
        assertThat(quantities, hasSize(prices.size()));

        BigDecimal itemTotal = BigDecimal.ZERO;
        for (int i = 0; i < prices.size(); i++) {
            itemTotal = itemTotal.add(prices.get(i).multiply(BigDecimal.valueOf(quantities.get(i))));
        }
        return itemTotal;
    }

    private String[] remainingItems() {
        return addedItems.stream().filter(item -> !removedItems.contains(item)).toArray(String[]::new);
    }
}
