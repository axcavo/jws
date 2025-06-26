package axcavo.jws;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Scrapper {
    private final String url;
    private final ScrapperConfig config;
    private final ScrapResultMapper mapper;


    protected Scrapper(String url, String name, ScrapperConfig config, ScrapResultMapper mapper) {
        this.url = url;
        this.config = config;
        this.mapper = mapper;
    }

    protected void log(String message) {
        System.out.println("[Scrapper]: " + message);
    }

    public ScrapResult scrap() throws IOException {
        Document document = Jsoup.connect(url).get();

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
