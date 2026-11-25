package com.prevaj.automation.api;

import com.prevaj.automation.config.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.restassured.RestAssured.given;

public class ReqResClient {

    private static final String API_KEY_HEADER = "x-api-key";

    private final boolean apiKeySent;
    private final RequestSpecification specification;

    public ReqResClient() {
        Optional<String> apiKey = ConfigReader.reqResApiKey();
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.reqResBaseUrl())
                .setBasePath(ConfigReader.reqResBasePath())
                .setConfig(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .connectTimeout(ConfigReader.reqResConnectTimeout())
                                .readTimeout(ConfigReader.reqResReadTimeout()))
                        .logConfig(LogConfig.logConfig().blacklistHeader(API_KEY_HEADER)));
        apiKey.ifPresent(key -> builder.addHeader(API_KEY_HEADER, key));
        apiKeySent = apiKey.isPresent();
        specification = builder.build();
    }

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

    /**
     * Explains an authentication (401, 403) or rate limit (429) rejection, so it is not mistaken for
     * an application defect. The message says whether an API key was sent but never includes the key.
     */
    public Optional<String> describeAccessRejection(Response response) {
        String apiKeyState = apiKeySent
                ? "an x-api-key header from REQRES_API_KEY was sent"
                : "no x-api-key header was sent because REQRES_API_KEY is not set";
        return switch (response.statusCode()) {
            case 401, 403 -> Optional.of("ReqRes rejected the request with HTTP %d (%s); check the API key and the ReqRes access rules"
                    .formatted(response.statusCode(), apiKeyState));
            case 429 -> Optional.of("ReqRes rate limit reached: HTTP 429, Retry-After %s (%s); wait before running the API tests again"
                    .formatted(Objects.requireNonNullElse(response.header("Retry-After"), "not provided"), apiKeyState));
            default -> Optional.empty();
        };
    }

    private Response postJson(String path, Map<String, String> body) {
        return given(specification).contentType(ContentType.JSON).body(body).post(path);
    }
}
