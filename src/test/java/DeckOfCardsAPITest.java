import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeckOfCardsAPITest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setUp() {

        // Set the path to your ChromeDriver executable
        System.setProperty("webdriver.chrome.driver",
            "/Users/vasya/IdeaProjects/CheckersAndCardsGame/src/test/resources/drivers/chromedriver");//--> please change user name path

        // Optional: You can configure Chrome options if needed
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run Chrome in headless mode (no GUI)

        // Initialize the WebDriver with ChromeDriver
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));   // Wait for up to 30 seconds

        // Navigate to the Deck of Cards website
        driver.get("https://deckofcardsapi.com/");
    }

    @Test
    public void checkForBlackjack() {

        // Check for the presence of the "Deck of Cards" text
        WebElement pageTitle = driver.findElement(By.xpath("//h1[contains(text(),'Deck of Cards')]"));
        assertTrue(pageTitle.isDisplayed());

        // Make API requests to create, shuffle, and draw cards
        Response createDeckResponse = RestAssured.get("https://deckofcardsapi.com/api/deck/new/");
        String deckId = createDeckResponse.jsonPath().getString("deck_id");

        Response shuffleDeckResponse = RestAssured.get("https://deckofcardsapi.com/api/deck/" + deckId + "/shuffle/");

        Response drawCardsResponse = RestAssured.get("https://deckofcardsapi.com/api/deck/" + deckId + "/draw/?count=6");
        String responseBody = drawCardsResponse.getBody().asString();

        // Split the card data for two players if there are enough elements
        String[] player1CardData = {};
        String[] player2CardData = {};
        String[] cards = responseBody.split("\n");

        if (cards.length >= 2) {
            player1CardData = cards[0].split(",");
            player2CardData = cards[1].split(",");
        } else {
            // Handle the case where there are not enough elements in the array
            System.err.println("Not enough card data for both players.");
        }

        // Calculate the scores for both players
        int player1Score = calculateHandScore(player1CardData);
        int player2Score = calculateHandScore(player2CardData);

        // Check for blackjack and log the result
        if (player1Score == 21) {
            System.out.println("Player 1 has blackjack!");
        }
        if (player2Score == 21) {
            System.out.println("Player 2 has blackjack!");
        }
    }

    private int calculateHandScore(String[] cardData) {

        int score = 0;
        for (String cardDatum : cardData) {
            String[] cardInfo = cardDatum.split(":");
            String cardValue = cardInfo[1].trim().replaceAll("\"", "");

            if (cardValue.equals("KING") || cardValue.equals("QUEEN") || cardValue.equals("JACK")) {
                score += 10;
            } else if (cardValue.equals("ACE")) {
                score += 11;
            } else {
                score += Integer.parseInt(cardValue);
            }
        }
        return score;
    }

    @AfterAll
    public static void tearDown() {
        // Close the WebDriver instance when the test is done
        driver.quit();
    }
}
