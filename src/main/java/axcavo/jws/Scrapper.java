package axcavo.jws;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Scrapper {
    private final String url;
    private final ScrapperConfig config;
    private final ScrapResultMapper mapper;


    protected Scrapper(String url, ScrapperConfig config, ScrapResultMapper mapper) {
        this.url = url;
        this.config = config;
        this.mapper = mapper;
    }

    protected void log(String message) {
        System.out.println("[Scrapper]: " + message);
    }

    public ScrapResult scrap() throws HttpStatusException, MalformedURLException, UnsupportedMimeTypeException, SocketTimeoutException, IOException {
        Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
            .timeout(10000)
            .followRedirects(true)
            .get();

        Elements elements = new Elements();
        for (String target : config.getCssQueries()) {
            elements.addAll(document.select(target));
        }

        return mapper.map(elements);
    }

    public String getUrl() {
        return url;
    }

    public ScrapperConfig getConfig() {
        return config;
    }
}
