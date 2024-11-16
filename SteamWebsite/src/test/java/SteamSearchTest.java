import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.*;
import java.util.List;
import java.time.Duration;

public class SteamSearchTest {
    public String baseUrl = "https://store.steampowered.com/";
    public WebDriver driver ;
    public SteamSearch steamSearch;

    List<SteamSearch.Game> games ;
    @FindBy(xpath = "//input[@id='store_nav_search_term']")
    WebElement searchBox;


    @BeforeTest
    public void setup()
    {
        System.out.println("Before Test executed");
        driver=new ChromeDriver();

        driver.manage().window().maximize();

        driver.get(baseUrl);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));

        steamSearch = new SteamSearch(driver);

        PageFactory.initElements(driver, this);
    }

    @Test(priority = 1, enabled = true)
    public void doSearchUsingDota2() throws InterruptedException {
        searchBox.sendKeys("Dota 2");
        searchBox.submit();

        //String actucal_searchResult = steamSearch.getsearchBoxValue();

        String actucal_searchResult = steamSearch.getFirstGameTitle();

        Assert.assertEquals(actucal_searchResult, "Dota 2");
        games = steamSearch.getGamesInfo(2);

        for (SteamSearch.Game game : games) {

            System.out.println("Title: " + game.title);

            System.out.print("Platform(s): ");
            for (int i = 0; i < game.platforms.size(); i++) {
                System.out.print(game.platforms.get(i));
                if (i < game.platforms.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();

            System.out.println("Review Summary: " + game.reviewSummary);
            System.out.println("Release Date: " + game.releaseDate);
            System.out.println("Price: " + game.price);
            System.out.println();
        }

    }
    @Test(priority = 2,enabled = true)
    public void doSearchUsing2ndResult() throws InterruptedException {

        //System.out.println("2nd Result Name: " + games.get(1).title);

        String second_search_item = games.get(1).title;

        searchBox.sendKeys(second_search_item);
        searchBox.submit();

        String actucal_searchItem = steamSearch.getFirstGameTitle();

        Assert.assertEquals(actucal_searchItem, second_search_item);

        List<SteamSearch.Game> secondGameListResult =  steamSearch.getGamesInfo(-1);


        for (SteamSearch.Game originalGame : games) {
            boolean isFound = false;

            for (SteamSearch.Game secondGame : secondGameListResult) {
                if (originalGame.title.equals(secondGame.title)) {

                    Assert.assertEquals(secondGame.releaseDate, originalGame.releaseDate, "Release date does not match for: " + originalGame.title);
                    Assert.assertEquals(secondGame.price, originalGame.price, "Price does not match for: " + originalGame.title);
                    Assert.assertEquals(secondGame.reviewSummary, originalGame.reviewSummary, "Review summary does not match for: " + originalGame.title);
                    Assert.assertTrue(secondGame.platforms.containsAll(originalGame.platforms), "Platforms do not match for: " + originalGame.title);

                    isFound = true;
                    break;
                }


            }
            if (isFound) {
                System.out.println("Game found in the second list: " + originalGame.title);
                Assert.assertTrue(isFound, "Game should be found in the second list.");
            } else {
                Assert.assertTrue(isFound, "Game not found in the second list: " + originalGame.title);
            }

        }
    }

    @AfterTest
    public void tearDown() throws InterruptedException
    {

        Thread.sleep(5000);
        driver.close();
        driver.quit();

    }
}
