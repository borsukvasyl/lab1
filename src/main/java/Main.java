import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        String baseUrl = "https://rozetka.com.ua/ua/usb-flash-memory/c80045/";
        parse(baseUrl);
    }

    public static void parse(String url) throws IOException {
        int numberOfPages = countPages(url);
        for (int pageNumber = 0; pageNumber < 1; pageNumber++) {
            String pageUrl = url + "page=" + pageNumber + "/";
            parsePages(pageUrl);
        }
    }

    public static int countPages(String pageUrl) throws IOException {
        Document doc = Jsoup.connect(pageUrl).get();
        Elements nums = doc.getElementsByClass("paginator-catalog-l-link");
        if (nums.size() > 0) {
            return Integer.parseInt(nums.get(nums.size() - 1).text());
        } else {
            return 0;
        }
    }

    public static void parsePages(String pageUrl) throws IOException {
        Document doc = Jsoup.connect(pageUrl).get();
        Elements tiles = doc.getElementsByClass("g-i-tile-i-title");
        System.out.println(tiles.size());
        for (Element tile : tiles) {
            String link = tile.getElementsByTag("a").attr("href");
            parseReviews(link);
        }
    }

    public static void parseReviews(String basePageUrl) throws IOException {
        int numberOfPages = countPages(basePageUrl);
        List sentiments = new ArrayList();

        for (int pageNumber = 0; pageNumber < numberOfPages + 1; pageNumber++) {
            String pageUrl = basePageUrl + "/page=" + pageNumber + "/";
            System.out.println(pageUrl);
            sentiments.addAll(parseReviewsPage(pageUrl));
        }
    }

    public static List parseReviewsPage(String pageUrl) throws IOException {
        Document doc = Jsoup.connect(pageUrl).get();
        Elements reviews = doc.getElementsByClass("pp-review-i");
        System.out.println(reviews.size());

        List sentiments = new ArrayList();
        for (Element review : reviews) {
            String stars = review.getElementsByClass("g-rating-stars-i").attr("content");
            if (!stars.equals("")) {
                String text = review.getElementsByClass("pp-review-text-i").get(0).text();
                sentiments.add(text);
            }
        }
        return sentiments;
    }
}
