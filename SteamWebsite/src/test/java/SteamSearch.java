import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SteamSearch {
    WebDriver driver;
    @FindBy(xpath = "//input[@name='displayterm']")
    WebElement search_box;
    @FindBy(css = ".title")
    WebElement gameTitle;

    @FindBy(css = ".responsive_search_name_combined")
    private List<WebElement> rows;

    public SteamSearch(WebDriver driver)
    {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

//    public String getsearchBoxValue(){
//       // System.out.println(search_box.getAttribute("value"));
//        return search_box.getAttribute("value");
//    }

    public String getFirstGameTitle(){
        return gameTitle.getText();
    }

    public List<Game> getGamesInfo(int limit) throws InterruptedException {
        List<Game> games = new ArrayList<>();
        int rowCount = (limit > 0) ? Math.min(limit, rows.size()) : rows.size();;
        for (int i = 0; i < rowCount; i++) {

            try {
                WebElement row = rows.get(i);
                Game game = new Game();

                game.title = row.findElement(By.cssSelector(".title")).getText();

                List<WebElement> platforms = row.findElements(By.cssSelector(".platform_img"));
                List<String> platformNames = new ArrayList<>();
                for (WebElement platform : platforms) {
                    platformNames.add(platform.getAttribute("class").split(" ")[1]);
                }
                game.platforms = platformNames;

                try {
                    WebElement reviewSummary = row.findElement(By.cssSelector(".search_review_summary"));
                    game.reviewSummary = reviewSummary.getAttribute("class").split(" ")[1];
                } catch (Exception e) {
                    game.reviewSummary = "Review Summary Not Found";
                }

                game.releaseDate = row.findElement(By.cssSelector(".col.search_released")).getText();

                try{
                    game.price = row.findElement(By.cssSelector(".discount_final_price")).getText();

                } catch (Exception e){
                    game.price = "NOT FOUND";
                }

                games.add(game);

            } catch (Exception e) {
                System.out.println("Error processing game: " + e.getMessage());
            }
        }

        return games;
    }
    public static class Game {
        String title;
        List<String> platforms;
        String reviewSummary;
        String releaseDate;
        String price;
    }

}
