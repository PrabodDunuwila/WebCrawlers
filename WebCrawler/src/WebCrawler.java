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

public class WebCrawler {

    private final Set<URL> links;
    private final long startTime;
    private final String filePath;

    public WebCrawler(final URL startURL, String filePath) {
        this.links = new HashSet<>();
        this.startTime = System.currentTimeMillis();
        this.filePath = filePath;
        createFile();
        crawl(initURLS(startURL));
    }

    private Set<URL> initURLS(final URL startURL) {
        return Collections.singleton(startURL);
    }

    private void crawl(final Set<URL> urls){
        urls.removeAll(this.links);
        if(!urls.isEmpty()){
            final Set<URL> newURLS = new HashSet<>();
            try{
                this.links.addAll(urls);
                for(final URL url: urls){
                    System.out.println("time : " + (System.currentTimeMillis() - this.startTime) + " connected to " + url);
                    writeToFile(url);
                    final Document document = Jsoup.connect(url.toString()).get();
                    final Elements linksOnPage = document.select("a[href]");
                    for(final Element element: linksOnPage){
                        final String urlText = element.attr("abs:href");
                        final URL discoveredURL = new URL(urlText);
                        newURLS.add(discoveredURL);
                    }
                }
            }catch(final Exception | Error ignored){
                //
            }
            crawl(newURLS);
        }
    }

    private void createFile(){
        try {
            File myObj = new File(filePath);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(URL url){
        try {
            FileWriter myWriter = new FileWriter(filePath, true);
            myWriter.write(String.valueOf(url));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        final WebCrawler crawler = new WebCrawler(new URL("https://www.facebook.com/"),
                "/home/dunu008/JavaWebCrawler/filename.txt");
    }

}
