import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginTest {
    @Test
    void testLoginSuccess() {
        WebDriver driver = DriverManager.getDriver();
        String clientAddr = DriverManager.getClientAddress();

        DriverManager.clearCookies();
        DriverManager.goTo("login");
        
        driver.findElement(By.className("login-input"))
                .sendKeys("testuser1");
        driver.findElement(By.className("password-input"))
                .sendKeys("password1");
        driver.findElement(By.className("button-login"))
                .click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().equals(clientAddr));
        assertEquals(driver.getCurrentUrl(), clientAddr);
    }

    @Test
    void testLoginWrongCredentials() {
        WebDriver driver = DriverManager.getDriver();
        String errorMsg = "Неверный логин или пароль";

        DriverManager.clearCookies();
        DriverManager.goTo("login");
        
        driver.findElement(By.className("login-input"))
                .sendKeys("wronguser1");
        driver.findElement(By.className("password-input"))
                .sendKeys("wrongpassword1");
        driver.findElement(By.className("button-login"))
                .click();

        WebElement loginErrorElem = driver.findElement(By.className("login-error-message"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> loginErrorElem.getText().equals(errorMsg));
        assertEquals(loginErrorElem.getText(), errorMsg);
    }

    @Test
    void testAccountSwitch() {
        WebDriver driver = DriverManager.getDriver();
        String clientAddr = DriverManager.getClientAddress();
        String loginAddr = clientAddr + "login";

        DriverManager.clearCookies();
        DriverManager.goTo("login");
        
        // Вход в аккаунт пользователя 1
        driver.findElement(By.className("login-input"))
                .sendKeys("testuser1");
        driver.findElement(By.className("password-input"))
                .sendKeys("password1");
        driver.findElement(By.className("button-login"))
                .click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().equals(clientAddr));
        assertEquals(driver.getCurrentUrl(), clientAddr);

        // Выход из аккаунта
        driver.findElement(By.className("exit-button"))
                .click();

        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().equals(loginAddr));
        assertEquals(driver.getCurrentUrl(), loginAddr);
        driver.navigate().refresh();

        // Вход в аккаунт пользователя 2
        driver.findElement(By.className("login-input"))
                .sendKeys("testuser2");
        driver.findElement(By.className("password-input"))
                .sendKeys("password2");
        driver.findElement(By.className("button-login"))
                .click();

        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().equals(clientAddr));
        assertEquals(driver.getCurrentUrl(), clientAddr);
    }
}
