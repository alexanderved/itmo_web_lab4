import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.SecureRandom;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegistrationTest {
    private String generateRandomString() {
        final String letters = 
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final SecureRandom random = new SecureRandom();
        final int length = 8;

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(letters.length());
            sb.append(letters.charAt(index));
        }
        return sb.toString();
    }

    private void register(WebDriver driver, String username, String password, String passwordRepeat) {
        DriverManager.clearCookies();

        // Вход на страницу регистрации
        DriverManager.goTo("login");
        driver.findElement(By.className("button-register"))
                .click();
        assertEquals(driver.getCurrentUrl(), DriverManager.getClientAddress() + "registration");

        // Регистрация
        driver.findElement(By.className("username-input"))
                .sendKeys(username);
        driver.findElement(By.className("password-input"))
                .sendKeys(password);
        driver.findElement(By.className("password-repeat-input"))
                .sendKeys(passwordRepeat);
        driver.findElement(By.className("button-registration"))
                .click();
    }

    private void waitError(WebDriver driver, String errorMsg) {
        WebElement registrationErrorElem = driver.findElement(By.className("registration-error-message"));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> registrationErrorElem.getText().equals(errorMsg));
        assertEquals(registrationErrorElem.getText(), errorMsg);
    }

    @Test
    void testRegisterSuccess() {
        WebDriver driver = DriverManager.getDriver();
        String clientAddr = DriverManager.getClientAddress();
        String loginAddr = clientAddr + "login";
        String newUsername = "testuser-" + generateRandomString();

        register(driver, newUsername, "password", "password");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().equals(loginAddr));
        assertEquals(driver.getCurrentUrl(), loginAddr);
    }

    @Test
    void testRegisterNonrepeatingPassword() {
        WebDriver driver = DriverManager.getDriver();
        String newUsername = "testuser0";
        String errorMsg = "Пароль и повторный пароль не совпадают";

        register(driver, newUsername, "password", "other-password");
        waitError(driver, errorMsg);
    }

    @Test
    void testRegisterTakenUsername() {
        WebDriver driver = DriverManager.getDriver();
        String newUsername = "testuser1";
        String errorMsg = String.format("Не удалось зарегистрировать нового пользователя: Имя пользователя '%s' занято", newUsername);

        register(driver, newUsername, "password", "password");
        waitError(driver, errorMsg);
    }
}
