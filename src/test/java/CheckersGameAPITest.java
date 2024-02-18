import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;;

public class CheckersGameAPITest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    public static JavascriptExecutor getExecutor() {
        return (JavascriptExecutor) driver;
    }

    @BeforeAll
    public static void setUp() {

        // Set the path to your ChromeDriver executable
        System.setProperty("webdriver.chrome.driver",
            (System.getProperty("user.dir") + "/src/test/resources/drivers/chromedriver"));

        // Optional: You can configure Chrome options if needed
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headed");   // Run Chrome in a headless mode (with GUI)
//        options.addArguments("--headless");   // Run Chrome in a headless mode (no GUI)

        // Initialize the WebDriver with ChromeDriver
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));   // Wait for up to 30 seconds

        // Navigate to the Checkers game page
        driver.get("https://www.gamesforthebrain.com/game/checkers/");
    }

    @Test
    public void playCheckersGame() {

        // Wait for the game to load
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));   // Wait for up to 30 seconds
        WebElement pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        assertThat(pageTitle.isDisplayed());
        assertThat(pageTitle.getText().equals("Checkers"));

        // Make 5 moves
        makeMove(6, 2);
        makeMove(2, 2);
        makeMove(7, 1);
        makeMove(3, 1);
        makeMove(4, 0);

        // Restart the game
        WebElement restartButton = driver.findElement(By.cssSelector("a[href='./']"));
        getExecutor().executeScript("arguments[0].click();", restartButton);
    }

    private void makeMove(int row, int column) {

        String cellSelector = String.format("[name='space%d%d']", row, column);

        // Click on the cell
        WebElement cell = driver.findElement(By.cssSelector(cellSelector));
        getExecutor().executeScript("arguments[0].click();", cell);

        // Wait for 2 sec (you can adjust this as needed)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int newRow = row - 1;
        int newColumn = column + 1;
        String availableMoveSelector = String.format("[name='space%d%d']", newRow, newColumn);

        // Check if the next cell isn't empty and click on it.
        WebElement nextCell = driver.findElement(By.cssSelector(availableMoveSelector));
        if (nextCell.getAttribute("src").equals("you1.gif")) {
            int newRow1 = row - 2;
            int newColumn1 = column + 2;
            String availableMoveSelectorWithJump = String.format("[name='space%d%d']", newRow1, newColumn1);
            WebElement cellWithJump = driver.findElement(By.cssSelector(availableMoveSelectorWithJump));
            getExecutor().executeScript("arguments[0].click();", cellWithJump);
        } else {
            getExecutor().executeScript("arguments[0].click();", nextCell);
        }

        // Wait for 2 sec (you can adjust this as needed).
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown() {
        // Close the WebDriver instance when the test is done
        driver.quit();
    }
}
