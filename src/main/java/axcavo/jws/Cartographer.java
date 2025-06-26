package axcavo.jws;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;

public class Cartographer {
    private final String name;
    private final String host;
    private final String urlFilter;

    public Cartographer(String name, String baseUrl, String urlFilter) {
        this.name = name;
        this.host = extractHost(baseUrl);
        this.urlFilter = urlFilter;
    }

    private String extractHost(String url) {
        try {
            URL u = URI.create(url).toURL();
            return u.getHost();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }

    private Scrapper scrapper(String url) {
        return new Scrapper(name, url, new ScrapperConfig(List.of("[href]")), new CartographerResultMapper());
    }

    public Set<String> chart(String url) {
        Scrapper scrapper = scrapper(url);
        Set<String> result = new HashSet<>();

        try {
            for (Object value : scrapper.scrap().getAll().values()) {
                result.add((String) value);
            }
        } catch (SocketTimeoutException e) {
            scrapper.log("Timeout exception accessing '" + scrapper.getUrl() + "'.");
        } catch (HttpStatusException e) {
            scrapper.log("Http status exception accessing '" + scrapper.getUrl() + "'.");
        } catch (MalformedURLException e) {
            scrapper.log("Url '" + scrapper.getUrl() + "' is malformed.");
        } catch (UnsupportedMimeTypeException e) {
            scrapper.log("Unsupported mime type accessing '" + scrapper.getUrl() + "'.");
        } catch (IOException e) {
            scrapper.log("IO exception accessing '" + scrapper.getUrl() + "'.");
        }

        scrapper.log("Successfully scrapped '" + url +"'.");
        return result;
    }

    public boolean isInScope(String url) {
        return url != null && url.startsWith("https://" + host) && url.contains(urlFilter);
    }
}
