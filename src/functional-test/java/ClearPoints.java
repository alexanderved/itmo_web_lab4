import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ClearPoints {
    WebDriver driver;

    @BeforeEach
    void initialize() {
        driver = DriverManager.getDriver();

        login(driver, "testuser1", "password1");
    }

    @Test
    void testClearUserPoints() {
        sendForm(driver, "0", "0", "1");
        driver.findElement(By.className("clear-button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("empty-message")));
    }

    @Test
    void testClearOtherUserPoints() {
        sendForm(driver, "0", "0", "1");

        login(driver, "testuser2", "password2");
        driver.findElement(By.className("clear-button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("empty-message")));

            fail("Cleared points of another user");
        } catch (TimeoutException ignore) {
            login(driver, "testuser1", "password1");
            driver.findElement(By.className("clear-button")).click();
        }
    }

    private void login(WebDriver driver, String username, String password) {
        String clientAddr = DriverManager.getClientAddress();

        DriverManager.clearCookies();
        DriverManager.goTo("login");
        
        driver.findElement(By.className("login-input"))
                .sendKeys(username);
        driver.findElement(By.className("password-input"))
                .sendKeys(password);
        driver.findElement(By.className("button-login"))
                .click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().equals(clientAddr));
    }

    private void selectCheckbox(WebDriver driver, int grid, String value) {
        driver.findElements(By.className("checkbox-grid-container"))
                .get(grid)
                .findElements(By.className("checkbox-item"))
                .stream()
                .filter((WebElement cb) -> {
                    return cb.findElement(By.className("checkbox-input")).getAttribute("value").equals(value);
                })
                .findFirst()
                .get()
                .click();
    }

    private void sendForm(WebDriver driver, String xValue, String yValue, String rValue) {
        selectCheckbox(driver, 0, xValue);
        driver.findElement(By.className("y-input")).sendKeys(yValue);
        selectCheckbox(driver, 1, rValue);

        driver.findElement(By.className("submit-button")).click();
    }
}
