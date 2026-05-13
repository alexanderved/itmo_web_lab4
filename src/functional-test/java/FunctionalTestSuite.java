import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;

@Suite
@SelectClasses({
    LoginTest.class,
    RegistrationTest.class,
})
public class FunctionalTestSuite {
    private static void createUsers() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder().build();
        String serverUrl = "http://localhost:8080/web-lab4/api/auth/registration";

        for (int i = 1; i < 3; ++i) {
            String jsonPayload = String.format("{\"username\": \"testuser%d\", \"password\": \"password%d\"}", i, i);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();

            httpClient.send(request, BodyHandlers.ofString());
        }
    }

    static void createDriver() throws MalformedURLException {
        String seleniumUrl = "http://localhost:4444/wd/hub";

        // Отключение предупреждения об утечке пароля
        final Map<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("credentials_enable_service", false);
        chromePrefs.put("profile.password_manager_enabled", false);
        chromePrefs.put("profile.password_manager_leak_detection", false);

        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("prefs", chromePrefs);

        RemoteWebDriver driver = new RemoteWebDriver(new URL(seleniumUrl), chromeOptions);
        DriverManager.setDriver(driver);
    }

    @BeforeSuite
    public static void createTestEnvironment() throws IOException, InterruptedException {
        createDriver();
        createUsers();
    }

    @AfterSuite
    public static void quitDriver() {
        DriverManager.quitDriver();
    }
}