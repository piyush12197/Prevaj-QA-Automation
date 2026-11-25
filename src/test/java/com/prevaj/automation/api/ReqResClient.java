package com.prevaj.automation.api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.time.Duration;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ReqResClient {

    private static final String BASE_URI = "https://reqres.in";
    private static final String BASE_PATH = "/api";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);

    private final RequestSpecification specification = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setBasePath(BASE_PATH)
            .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                    .connectTimeout(CONNECT_TIMEOUT)
                    .readTimeout(READ_TIMEOUT)))
            .build();

    public Response getUsers(int page) {
        return given(specification).queryParam("page", page).get("/users");
    }

    public Response getUsersWithDelay(int delaySeconds) {
        return given(specification).queryParam("delay", delaySeconds).get("/users");
    }

    public Response getUser(int userId) {
        return given(specification).get("/users/{id}", userId);
    }

    public Response createUser(String name, String job) {
        return postJson("/users", Map.of("name", name, "job", job));
    }

    public Response login(String email, String password) {
        return postJson("/login", Map.of("email", email, "password", password));
    }

    private Response postJson(String path, Map<String, String> body) {
        return given(specification).contentType(ContentType.JSON).body(body).post(path);
    }
}
