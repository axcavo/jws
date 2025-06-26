package axcavo.jws;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> targets = List.of("[href]");
        ScrapperConfig config = new ScrapperConfig(targets);

        ScrapResultMapper mapper = new ScrapResultMapper() {
            @Override
            public ScrapResult map(Elements elements) {
                ScrapResult scrapResult = new ScrapResult();

                int i = 0;
                for (Element element : elements) {
                    scrapResult.put("href"+i, element.absUrl("href"));
                    i++;
                }
                return scrapResult;
            }
        };

        Scrapper scrapper = new Scrapper("http://www.gol.gg/esports/home/", "test1", config, mapper);
        ScrapResult result = scrapper.scrap();

        System.out.println(result.getAll());
    }
}