import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {

    private final Set<URL> links;
    private final long startTime;
    private final String urlFilePath;
    private final String emailFilePath;

    public WebCrawler(final URL startURL) {
        this.links = new HashSet<>();
        this.startTime = System.currentTimeMillis();
        this.urlFilePath = "/home/dunu008/JavaWebCrawler/urlsScrapedFile.txt";
        this.emailFilePath = "/home/dunu008/JavaWebCrawler/emailsScrapedFile.txt";
        createFile(this.urlFilePath);
        createFile(this.emailFilePath);
        crawl(initURLS(startURL));
    }

    private Set<URL> initURLS(final URL startURL) {
        return Collections.singleton(startURL);
    }

    private void crawl(final Set<URL> urls) {
        urls.removeAll(this.links);
        if (!urls.isEmpty()) {
            final Set<URL> newURLS = new HashSet<>();
            try {
                this.links.addAll(urls);
                for (final URL url : urls) {
                    System.out.println("time : " + (System.currentTimeMillis() - this.startTime) + " connected to "
                            + url);
                    writeToFile(url.toString(), urlFilePath);
                    final Document document = Jsoup.connect(url.toString()).get();
                    scrapeEmails(document);
                    final Elements linksOnPage = document.select("a[href]");
                    for (final Element element : linksOnPage) {
                        final String urlText = element.attr("abs:href");
                        final URL discoveredURL = new URL(urlText);
                        newURLS.add(discoveredURL);
                    }
                }
            } catch (final Exception | Error ignored) {
                System.out.println("Exception");
            }
            crawl(newURLS);
        }
    }

    private void scrapeEmails(Document doc) {
        Matcher matcher = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(doc.toString());
        System.out.println("Email scrape");
        while (matcher.find()) {
            writeToFile(matcher.group(), emailFilePath);
        }
    }

    private void createFile(String filePath) {
        try {
            File myObj = new File(filePath);
            if (myObj.exists() && myObj.isFile()) {
                myObj.delete();
                System.out.println("Delete existing content.");
            }
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(String text, String filePath) {
        try {
            FileWriter myWriter = new FileWriter(filePath, true);
            myWriter.write(text + "\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        final WebCrawler crawler = new WebCrawler(
                new URL("https://science.kln.ac.lk/depts/im/index.php/staff/academic-staff"));
    }

}
