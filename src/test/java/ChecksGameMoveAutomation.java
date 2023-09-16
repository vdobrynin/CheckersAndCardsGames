import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;;

public class ChecksGameMoveAutomation {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setUp() {

        // Set the path to your ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "/Users/vasya/IdeaProjects/CheckersGame/src/test/resources/drivers/chromedriver");

        // Optional: You can configure Chrome options if needed
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headed"); // Run Chrome in headless mode (with GUI)
//        options.addArguments("--headless"); // Run Chrome in headless mode (no GUI)

        // Initialize the WebDriver with ChromeDriver
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Wait for up to 30 seconds

        // Navigate to the Checkers game page
        driver.get("https://www.gamesforthebrain.com/game/checkers/");
    }

    @Test
    public void playCheckersGame() {

        // Wait for the game to load
        wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Wait for up to 30 seconds
        WebElement pageTitle = wait
            .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        assertThat(pageTitle.isDisplayed());
        assertThat(pageTitle.getText().equals("Checkers"));

        // Make 5 moves
        makeMove(2, 2);
        makeMove(3, 1);
        makeMove(4, 0);
        makeMove(7, 3);
        makeMove(1, 3);

        // Restart the game
        WebElement restartButton = driver.findElement(By.cssSelector("a[href='./']"));
//        WebElement restartButton = driver.findElement(By.linkText("Restart"));
        restartButton.click();

        // Wait for the "Make a move" button to be visible
//        WebElement makeMoveButton = wait
//            .until(ExpectedConditions
//                .visibilityOfElementLocated(By.cssSelector("button[name='move']")));
//        assertThat(makeMoveButton.isDisplayed());
    }

    @AfterAll
    public static void tearDown() {
        // Close the WebDriver instance when the test is done
        driver.quit();
    }

    private void makeMove(int row, int column) {

        String cellSelector = String.format("[name='space%d%d']", row, column);

        // Click on the cell
        WebElement cell = driver.findElement(By.cssSelector(cellSelector));
        cell.click();

        // Wait for 2 seconds (you can adjust this as needed)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int newRow = row - 1;
        int newColumn = column + 1;
        String availableMoveSelector = String.format("[name='space%d%d']", newRow, newColumn);

        // Check if the next cell is not empty and click on it
        WebElement nextCell = driver.findElement(By.cssSelector(availableMoveSelector));
        if (nextCell.getAttribute("src").equals("you1.gif")) {
            int newRow1 = row - 2;
            int newColumn1 = column + 2;
            String availableMoveSelectorWithJump = String.format("[name='space%d%d']", newRow1, newColumn1);
            WebElement cellWithJump = driver.findElement(By.cssSelector(availableMoveSelectorWithJump));
            cellWithJump.click();
        } else {
            nextCell.click();
        }

        // Wait for 2 seconds (you can adjust this as needed)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
