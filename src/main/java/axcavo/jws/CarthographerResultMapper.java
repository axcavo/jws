package axcavo.jws;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CarthographerResultMapper implements ScrapResultMapper {
    @Override
    public ScrapResult map(Elements elements) {
        ScrapResult scrapResult = new ScrapResult();

        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            scrapResult.put(String.valueOf(i), element.absUrl("href"));
            
        }
        return scrapResult;
    }
}
