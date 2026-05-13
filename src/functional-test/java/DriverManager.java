import org.openqa.selenium.WebDriver;

public class DriverManager {
    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException("Driver not initialised.");
        }
        return driver;
    }

    static void setDriver(WebDriver webDriver) {
        driver = webDriver;
    }

    static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public static String getClientAddress() {
        return "http://client:5173/";
    }

    public static void goTo(String page) {
        driver.get(getClientAddress() + page);
    }

    public static void clearCookies() {
        driver.manage().deleteAllCookies();
    }
}