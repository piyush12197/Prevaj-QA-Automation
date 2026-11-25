package com.prevaj.automation.steps;

import com.prevaj.automation.api.ReqResClient;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class ApiSteps {

    private final ReqResClient client = new ReqResClient();
    private final List<Integer> collectedUserIds = new ArrayList<>();
    private Response response;
    private int requestedUserId;

    @Given("I get the default list of users on the 1st page")
    public void getDefaultListOfUsersOnFirstPage() {
        response = client.getUsers(1);
    }

    @When("I get the list of all users within every page")
    public void getListOfAllUsersWithinEveryPage() {
        collectedUserIds.addAll(userIdsFrom(response));
        int totalPages = response.jsonPath().getInt("total_pages");
        for (int page = 2; page <= totalPages; page++) {
            collectedUserIds.addAll(userIdsFrom(client.getUsers(page)));
        }
    }

    @Then("I should see that the total users count equals the number of unique user ids")
    public void totalUsersCountEqualsNumberOfUniqueUserIds() {
        int totalUsers = response.jsonPath().getInt("total");
        assertThat(collectedUserIds, is(not(empty())));
        assertThat(collectedUserIds, everyItem(is(notNullValue())));
        assertThat(collectedUserIds, hasSize(totalUsers));
        assertThat(new HashSet<>(collectedUserIds), hasSize(collectedUserIds.size()));
    }

    @Given("I make a search for user {int}")
    public void searchForUser(int userId) {
        requestedUserId = userId;
        response = client.getUser(userId);
    }

    @Then("I should see the following user data")
    public void shouldSeeUserData(DataTable expectedData) {
        assertThat(response.jsonPath().getInt("data.id"), is(requestedUserId));
        assertResponseFields("data.", expectedData.asMaps().getFirst());
    }

    @Given("I create a user with name {string} and job {string}")
    public void createUser(String name, String job) {
        response = client.createUser(name, job);
    }

    @Then("the response should contain the following data")
    public void responseShouldContainData(DataTable expectedData) {
        assertResponseFields("", expectedData.asMaps().getFirst());
    }

    @Then("the response should contain a generated id and creation timestamp")
    public void responseShouldContainGeneratedIdAndCreationTimestamp() {
        String createdAt = response.jsonPath().getString("createdAt");
        assertThat(response.jsonPath().getString("id"), not(blankOrNullString()));
        assertThat(createdAt, not(blankOrNullString()));
        assertThat(Instant.parse(createdAt), is(notNullValue()));
    }

    @Given("I log in successfully/unsuccessfully with the following data")
    public void logInWithData(DataTable credentials) {
        Map<String, String> row = credentials.asMaps().getFirst();
        String password = Objects.requireNonNullElse(row.get("Password"), "");
        response = client.login(row.get("Email"), password);
    }

    @Then("I should see the following response message")
    public void shouldSeeResponseMessage(DataTable expectedMessage) {
        assertResponseFields("", expectedMessage.asMaps().getFirst());
    }

    @Given("I wait for the user list to load with a delay of {int} seconds")
    public void waitForDelayedUserList(int delaySeconds) {
        response = client.getUsersWithDelay(delaySeconds);
    }

    @Then("I should see that every user has a unique id")
    public void everyUserHasUniqueId() {
        List<Integer> userIds = response.jsonPath().getList("data.id", Integer.class);
        assertThat(userIds, is(not(empty())));
        assertThat(userIds, everyItem(is(notNullValue())));
        assertThat(new HashSet<>(userIds), hasSize(userIds.size()));
    }

    @Then("I should get a response code of {int}")
    public void shouldGetResponseCode(int statusCode) {
        assertStatusCode(response, statusCode);
    }

    private List<Integer> userIdsFrom(Response page) {
        assertStatusCode(page, 200);
        return page.jsonPath().getList("data.id", Integer.class);
    }

    private void assertResponseFields(String pathPrefix, Map<String, String> expectedFields) {
        expectedFields.forEach((field, expectedValue) ->
                assertThat(field, response.jsonPath().getString(pathPrefix + field), equalTo(expectedValue)));
    }

    private void assertStatusCode(Response response, int statusCode) {
        client.describeAccessRejection(response)
                .ifPresent(rejection -> assertThat(rejection, response.statusCode(), is(statusCode)));
        response.then().log().ifValidationFails().statusCode(statusCode);
    }
}
