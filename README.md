# Prevaj QA Automation

A Cucumber BDD automation framework for testing the ReqRes REST API and SauceDemo web application.

## Technology Stack

- Java 21
- Gradle 9.9.2
- Cucumber JVM 8.1.0
- JUnit Platform 6.1.3
- REST Assured 6.3.0
- Selenium WebDriver 4.54.0
- Hamcrest 3.0

## Prerequisites

- Java 21 JDK
- Google Chrome or Mozilla Firefox
- Internet connection

The Gradle Wrapper is included. A separate Gradle installation is not required. Browser drivers are managed by Selenium Manager.

## How to Run

Windows:

```powershell
.\gradlew.bat clean test
```

Linux/macOS:

```bash
./gradlew clean test
```

### Additional Execution Options (Windows)

| Execution | Command |
|---|---|
| API only | `.\gradlew.bat test "-Dcucumber.filter.tags=@api"` |
| UI only | `.\gradlew.bat test "-Dcucumber.filter.tags=@ui"` |
| Headless | `.\gradlew.bat test "-Dui.headless=true"` |
| Firefox | `.\gradlew.bat test "-Dui.browser=firefox"` |

## Configuration

Default settings are available in `src/test/resources/config.properties`.

| Property | Default |
|---|---|
| reqres.base.url | https://reqres.in |
| reqres.base.path | /api |
| reqres.connect.timeout.seconds | 10 |
| reqres.read.timeout.seconds | 30 |
| saucedemo.base.url | https://www.saucedemo.com/ |
| ui.browser | chrome |
| ui.headless | false |
| ui.explicit.wait.seconds | 10 |

Configuration priority:

System properties > Environment variables > Configuration file.

Environment variables use uppercase property names with dots replaced by underscores.

### ReqRes API Key

The classic ReqRes API endpoints were successfully tested without an API key. If required, an API key can be provided through the environment variable:

```powershell
$env:REQRES_API_KEY = "<your-api-key>"
.\gradlew.bat clean test
Remove-Item Env:REQRES_API_KEY
```

The framework sends the key through the `x-api-key` header when configured. No API key is hardcoded or exposed in request logs.

Authentication failures and rate-limit responses are reported with appropriate error messages.

Reference: https://reqres.in/docs

## Framework Structure

```text
src/test/
├── java/com/prevaj/automation/
│   ├── api/
│   ├── cart/
│   ├── config/
│   ├── driver/
│   ├── pages/
│   ├── runners/
│   └── steps/
└── resources/
    ├── config.properties
    └── features/
        ├── API-Test.feature
        └── UI-Test.feature
```

## Design Choices

- **Page Object Model:** Separate page classes for UI interactions and locators.
- **BasePage:** Centralized explicit waits and reusable element interaction methods.
- **DriverFactory:** Browser creation with Chrome, Firefox and headless support.
- **ConfigReader:** External configuration with runtime overrides.
- **ReqResClient:** Reusable API requests and authentication handling.
- **Basket and PriceCalculator:** Separate cart tracking and monetary calculations using BigDecimal.
- **Cucumber Hooks:** Scenario-level browser setup, failure screenshots and browser cleanup. API scenarios do not launch a browser.
- **Step Definitions:** Focus on scenario execution and assertions rather than low-level implementation.

## Bugs Identified and Fixed

| Issue | Fix |
|---|---|
| Successful login scenario used unsuccessful login wording. | Corrected the step wording. |
| Create-user parameters and response table were ambiguous. | Corrected parameter formatting and added generated ID and timestamp validation. |
| Missing-password response table was incorrectly formatted. | Corrected the table and error-message assertion. |
| Pagination and user-count validation were incomplete. | Added dynamic pagination and unique user ID validation. |
| Delayed-response scenario was unclear. | Added a 3-second delay parameter and HTTP status validation. |
| Cart wording was incorrect after product removal. | Corrected wording and validated remaining products. |
| Incorrect cart contents could pass subtotal validation. | Added exact product and quantity assertions. |
| Tax calculation wording was ambiguous. | Validated 8% tax on the item subtotal with monetary rounding. |
| Chrome password warning interrupted UI execution. | Disabled the warning for the temporary automation browser profile. |

These findings include wording corrections, requirement clarifications, assertion improvements and an environment issue.

## Test Reports

Reports are generated after execution:

| Report | Location |
|---|---|
| Cucumber HTML | `build/reports/cucumber/cucumber.html` |
| Cucumber JSON | `build/reports/cucumber/cucumber.json` |
| Gradle HTML | `build/reports/tests/test/index.html` |
| Gradle XML | `build/test-results/test/` |

For failed UI scenarios, a screenshot is automatically captured and attached to the Cucumber reports before closing the browser.

Reports are regenerated on each clean test execution. The build directory is excluded from Git.

## Test Results

Live execution on November 25, 2026:

| Suite | Passed | Failed | Skipped |
|---|---:|---:|---:|
| API | 8 | 0 | 0 |
| UI | 1 | 0 | 0 |
| **Total** | **9** | **0** | **0** |

Command: `.\gradlew.bat clean test`

Build result: **SUCCESSFUL**

Both suites were executed against live applications without mocked responses. ReqRes tests passed without an API key.

Automatic failure screenshot capture was also verified through a separate intentional failure. The original test expectation was restored before the successful final execution.