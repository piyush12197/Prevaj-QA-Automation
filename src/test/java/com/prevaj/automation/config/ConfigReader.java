package com.prevaj.automation.config;

import com.prevaj.automation.driver.Browser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

/**
 * Reads test configuration. Each value is taken from the first non-blank source in this order:
 * JVM system property, environment variable (key upper-cased, dots replaced by underscores), config.properties.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final String API_KEY_ENVIRONMENT_VARIABLE = "REQRES_API_KEY";
    private static final long MAX_TIMEOUT_SECONDS = 3600;
    private static final Properties FILE_PROPERTIES = loadConfigFile();

    private ConfigReader() {
    }

    public static String reqResBaseUrl() {
        return httpUrl("reqres.base.url");
    }

    public static String reqResBasePath() {
        Setting setting = setting("reqres.base.path");
        if (!setting.value().startsWith("/")) {
            throw setting.invalid("a path starting with '/'");
        }
        return setting.value();
    }

    public static Duration reqResConnectTimeout() {
        return timeout("reqres.connect.timeout.seconds");
    }

    public static Duration reqResReadTimeout() {
        return timeout("reqres.read.timeout.seconds");
    }

    /**
     * The optional ReqRes API key, read only from the environment so it never lives in a committed file.
     */
    public static Optional<String> reqResApiKey() {
        return Optional.ofNullable(System.getenv(API_KEY_ENVIRONMENT_VARIABLE))
                .map(String::strip)
                .filter(apiKey -> !apiKey.isEmpty());
    }

    public static String sauceDemoBaseUrl() {
        return httpUrl("saucedemo.base.url");
    }

    public static Browser browser() {
        Setting setting = setting("ui.browser");
        return Arrays.stream(Browser.values())
                .filter(browser -> browser.name().equalsIgnoreCase(setting.value()))
                .findFirst()
                .orElseThrow(() -> setting.invalid("one of " + Arrays.toString(Browser.values()).toLowerCase(Locale.ROOT)));
    }

    public static boolean headless() {
        Setting setting = setting("ui.headless");
        return switch (setting.value().toLowerCase(Locale.ROOT)) {
            case "true" -> true;
            case "false" -> false;
            default -> throw setting.invalid("true or false");
        };
    }

    public static Duration explicitWait() {
        return timeout("ui.explicit.wait.seconds");
    }

    private static String httpUrl(String key) {
        Setting setting = setting(key);
        try {
            URI uri = new URI(setting.value());
            String scheme = uri.getScheme();
            if (("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) && uri.getHost() != null) {
                return setting.value();
            }
        } catch (URISyntaxException ignored) {
            // Reported below together with every other invalid URL.
        }
        throw setting.invalid("an absolute http or https URL");
    }

    private static Duration timeout(String key) {
        Setting setting = setting(key);
        try {
            long seconds = Long.parseLong(setting.value());
            if (seconds >= 1 && seconds <= MAX_TIMEOUT_SECONDS) {
                return Duration.ofSeconds(seconds);
            }
        } catch (NumberFormatException ignored) {
            // Reported below together with out-of-range values.
        }
        throw setting.invalid("a whole number of seconds from 1 to " + MAX_TIMEOUT_SECONDS);
    }

    private static Setting setting(String key) {
        String systemProperty = System.getProperty(key);
        if (isSet(systemProperty)) {
            return new Setting(key, systemProperty.strip(), "JVM system property " + key);
        }
        String environmentVariable = key.toUpperCase(Locale.ROOT).replace('.', '_');
        String environmentValue = System.getenv(environmentVariable);
        if (isSet(environmentValue)) {
            return new Setting(key, environmentValue.strip(), "environment variable " + environmentVariable);
        }
        String fileValue = FILE_PROPERTIES.getProperty(key);
        if (isSet(fileValue)) {
            return new Setting(key, fileValue.strip(), CONFIG_FILE);
        }
        throw new IllegalStateException("Missing configuration value '%s': set it in %s, environment variable %s or JVM system property %s"
                .formatted(key, CONFIG_FILE, environmentVariable, key));
    }

    private static boolean isSet(String value) {
        return value != null && !value.isBlank();
    }

    private static Properties loadConfigFile() {
        try (InputStream stream = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (stream == null) {
                throw new IllegalStateException(CONFIG_FILE + " was not found on the test classpath");
            }
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + CONFIG_FILE, e);
        }
    }

    private record Setting(String key, String value, String source) {

        IllegalArgumentException invalid(String expected) {
            return new IllegalArgumentException("Invalid configuration value '%s' for '%s' from %s: expected %s"
                    .formatted(value, key, source, expected));
        }
    }
}
