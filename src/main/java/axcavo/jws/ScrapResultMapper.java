package axcavo.jws;

import org.jsoup.select.Elements;

@FunctionalInterface
public interface ScrapResultMapper {
    ScrapResult map(Elements elements);
}
