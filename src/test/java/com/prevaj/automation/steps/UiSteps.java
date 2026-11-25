package com.prevaj.automation.steps;

import com.prevaj.automation.cart.Basket;
import com.prevaj.automation.cart.PriceCalculator;
import com.prevaj.automation.driver.DriverFactory;
import com.prevaj.automation.pages.CartPage;
import com.prevaj.automation.pages.CheckoutInfoPage;
import com.prevaj.automation.pages.CheckoutOverviewPage;
import com.prevaj.automation.pages.InventoryPage;
import com.prevaj.automation.pages.LoginPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hamcrest.Matcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class UiSteps {

    private final Basket basket = new Basket();
    private WebDriver driver;
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutInfoPage checkoutInfoPage;
    private CheckoutOverviewPage checkoutOverviewPage;

    @Before("@ui")
    public void startBrowser() {
        driver = DriverFactory.createDriver();
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver);
        checkoutInfoPage = new CheckoutInfoPage(driver);
        checkoutOverviewPage = new CheckoutOverviewPage(driver);
    }

    /**
     * Attaches a screenshot to a failed scenario, then always quits the browser, even if the screenshot fails.
     */
    @After("@ui")
    public void quitBrowser(Scenario scenario) {
        if (driver == null) {
            return;
        }
        try {
            if (scenario.isFailed()) {
                attachScreenshot(scenario);
            }
        } finally {
            driver.quit();
        }
    }

    private void attachScreenshot(Scenario scenario) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot at failure");
        } catch (WebDriverException e) {
            // Keep the original failure as the reported cause. The raw message leaves out Selenium's host details.
            scenario.log("Could not capture a failure screenshot: %s: %s"
                    .formatted(e.getClass().getSimpleName(), e.getRawMessage()));
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
        for (String productName : productNames) {
            inventoryPage.addToCart(productName);
            basket.add(productName);
        }
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
        List<String> cartItems = cartPage.getItemNames();
        List<Integer> quantities = cartPage.getItemQuantities();
        assertThat(cartItems, containsBasketProducts());
        assertThat(quantities, hasSize(basket.productNames().size()));
        assertThat(quantities, everyItem(is(expectedQuantity)));
        assertThat(quantities, equalTo(basket.quantitiesOf(cartItems)));
    }

    @And("I remove the following item")
    public void removeItems(List<String> productNames) {
        for (String productName : productNames) {
            cartPage.remove(productName);
            basket.remove(productName);
        }
    }

    @And("I should see {int} items in the shopping cart")
    public void shouldSeeItemsInShoppingCart(int itemCount) {
        List<String> cartItems = cartPage.getItemNames();
        assertThat(cartPage.getBadgeCount(), is(itemCount));
        assertThat(cartItems, hasSize(itemCount));
        assertThat(cartItems, containsBasketProducts());
        assertThat(cartPage.getItemQuantities(), equalTo(basket.quantitiesOf(cartItems)));
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
        assertThat(checkoutOverviewPage.getItemTotal(), comparesEqualTo(expectedItemTotal()));
    }

    @And("a tax rate of {int}% should be applied to the item total")
    public void taxRateShouldBeAppliedToItemTotal(int taxRatePercent) {
        BigDecimal expectedTax = PriceCalculator.tax(expectedItemTotal(), taxRatePercent);
        assertThat(checkoutOverviewPage.getTax(), comparesEqualTo(expectedTax));
    }

    /**
     * Checks that the overview lists exactly the expected products and quantities, then totals
     * the listed prices multiplied by the expected quantities.
     */
    private BigDecimal expectedItemTotal() {
        List<String> overviewItems = checkoutOverviewPage.getItemNames();
        List<BigDecimal> prices = checkoutOverviewPage.getItemPrices();
        List<Integer> expectedQuantities = basket.quantitiesOf(overviewItems);
        assertThat(overviewItems, containsBasketProducts());
        assertThat(prices, is(not(empty())));
        assertThat(prices, hasSize(overviewItems.size()));
        assertThat(checkoutOverviewPage.getItemQuantities(), equalTo(expectedQuantities));
        return PriceCalculator.itemTotal(prices, expectedQuantities);
    }

    private Matcher<Iterable<? extends String>> containsBasketProducts() {
        return containsInAnyOrder(basket.productNames().toArray(String[]::new));
    }
}
