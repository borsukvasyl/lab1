import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        String baseUrl = "https://rozetka.com.ua/ua/usb-flash-memory/c80045/";
        parse(baseUrl);
    }

    public static void parse(String url) throws IOException {
        new File("data").mkdirs();

        int numberOfPages = countPages(url);
        for (int pageNumber = 1; pageNumber < numberOfPages + 1; pageNumber++) {
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
        Elements tiles = doc.getElementsByClass("g-i-tile-i-title").select("a");
        System.out.println(tiles.size());

        for (Element tile : tiles) {
            String link = tile.attr("abs:href") + "comments/";
            parseReviews(link);
        }
    }

    public static void parseReviews(String basePageUrl) throws IOException {
        int numberOfPages = countPages(basePageUrl);
        List sentiments = new ArrayList();

        for (int pageNumber = 0; pageNumber < numberOfPages; pageNumber++) {
            String pageUrl = basePageUrl + "/page=" + pageNumber + "/";
            sentiments.addAll(parseReviewsPage(pageUrl));
        }

        String filename = "data/" + basePageUrl.split("/")[4] + ".csv";
        PrintWriter writer = new PrintWriter(filename);
        for (Object line : sentiments) {
            writer.write(line.toString());
        }
        writer.close();
        System.out.println(sentiments.size() + " from " + basePageUrl);
    }

    public static List parseReviewsPage(String pageUrl) throws IOException {
        Document doc = Jsoup.connect(pageUrl).get();
        Elements reviews = doc.getElementsByClass("pp-review-i").select("article");

        List sentiments = new ArrayList();
        for (Element review : reviews) {
            String stars = review.getElementsByClass("g-rating-stars-i").attr("content");
            if (!stars.equals("")) {
                String text = stars + ", \"" + review.getElementsByClass("pp-review-text-i").get(0).text() + "\"\n";
                sentiments.add(text);
            }
        }
        return sentiments;
    }
}