import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AddPointsTest {
    WebDriver driver;
    String rValue;

    @BeforeEach
    void initialize() {
        driver = DriverManager.getDriver();
        login(driver, "testuser1", "password1");
        
        rValue = chooseRValue(driver);
    }

    @Test
    void testClickOnSphere() {
        selectCheckbox(driver, 1, rValue);
        clickOnCanvas(driver, 0, 75);
        checkAddedPoint(driver, rValue);
    }

    @Test
    void testClickOutsideSphere() {
        selectCheckbox(driver, 1, rValue);
        clickOnCanvas(driver, 250, 250);

        WebElement prevRElem = driver.findElements(By.className("table-cell")).get(2);
        assertNotEquals(prevRElem.getText(), rValue);
    }

    @Test
    void testClickDisabledSphere() {
        clickOnCanvas(driver, 0, 75);

        WebElement prevRElem = driver.findElements(By.className("table-cell")).get(2);
        assertNotEquals(prevRElem.getText(), rValue);
    }

    @Test
    void testSendFormSuccess() {
        sendForm(driver, "0", "0", rValue);
        checkAddedPoint(driver, rValue);
    }

    @Test
    void testSendFormEmptyFields() {
        driver.findElement(By.className("submit-button")).click();

        assertFalse(driver.findElement(By.id("x-input-error")).getText().isEmpty());
        assertFalse(driver.findElement(By.id("y-input-error")).getText().isEmpty());
        assertFalse(driver.findElement(By.id("r-input-error")).getText().isEmpty());
    }

    @Test
    void testSendFormWrongFormat() {
        sendForm(driver, "0", "abc", rValue);

        assertFalse(driver.findElement(By.id("y-input-error")).getText().isEmpty());
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
        assertEquals(driver.getCurrentUrl(), clientAddr);
    }

    private String chooseRValue(WebDriver driver) {
        try {
            WebElement prevRElem = driver.findElements(By.className("table-cell")).get(2);
            if (prevRElem.getText().equals("1")) {
                return "2";
            }
        } catch (IndexOutOfBoundsException ignore) {
        }

        return "1";
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

    private void clickOnCanvas(WebDriver driver, int xOffset, int yOffset) {
        WebElement canvasElem = driver.findElement(By.id("webgl-graph"));

        Actions builder = new Actions(driver);
        builder.moveToElement(canvasElem, xOffset, yOffset)
               .click()
               .build()
               .perform();
    }

    private void checkAddedPoint(WebDriver driver, String rValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By topRowLocator = By.xpath("(//tbody//tr)[1]");

        ExpectedCondition<Boolean> condition = new ExpectedCondition<>() {
            @Override
            public Boolean apply(WebDriver driver) {
                WebElement row = driver.findElement(topRowLocator);

                return row.getText().split(" ")[2].equals(rValue);
            }
        };
        
        wait.until(condition);

        WebElement prevRElem = driver.findElements(By.className("table-cell")).get(2);
        assertEquals(prevRElem.getText(), rValue);
    }
}
