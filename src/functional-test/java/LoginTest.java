import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginTest {
    WebDriver driver;
    String clientAddr;

    @BeforeEach
    void initialize() {
        driver = DriverManager.getDriver();
        clientAddr = DriverManager.getClientAddress();

        DriverManager.clearCookies();
        DriverManager.goTo("login");
    }

    @Test
    void testLoginSuccess() {
        login(driver, "testuser1", "password1");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().equals(clientAddr));
    }

    @Test
    void testLoginWrongUsername() {
        login(driver, "wronguser1", "password1");

        String errorMsg = "Неверный логин или пароль";
        WebElement loginErrorElem = driver.findElement(By.className("login-error-message"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> loginErrorElem.getText().equals(errorMsg));
    }

    @Test
    void testLoginWrongPassword() {
        login(driver, "testuser1", "wrongpassword1");

        String errorMsg = "Неверный логин или пароль";
        WebElement loginErrorElem = driver.findElement(By.className("login-error-message"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> loginErrorElem.getText().equals(errorMsg));
    }

    @Test
    void testAccountSwitch() {
        // Вход в аккаунт пользователя 1
        login(driver, "testuser1", "password1");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().equals(clientAddr));

        // Выход из аккаунта
        driver.findElement(By.className("exit-button"))
                .click();

        String loginAddr = clientAddr + "login";

        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().equals(loginAddr));

        driver.navigate().refresh();

        // Вход в аккаунт пользователя 2
        login(driver, "testuser2", "password2");

        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().equals(clientAddr));
    }

    void login(WebDriver driver, String username, String password) {
        driver.findElement(By.className("login-input"))
                .sendKeys(username);
        driver.findElement(By.className("password-input"))
                .sendKeys(password);
        driver.findElement(By.className("button-login"))
                .click();
    }
}
