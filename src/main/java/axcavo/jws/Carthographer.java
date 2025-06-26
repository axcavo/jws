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

public class Carthographer {
    private final String url;
    private final String host;

    public Carthographer(String url) {
        this.url = url;
        this.host = getHost(url);
    }

    private String getHost(String url) {
        try {
            URL u = URI.create(url).toURL();
            return u.getHost();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }

    private Scrapper scrapper(String url) {
        return new Scrapper(url, scrapperConfig(), new CarthographerResultMapper());
    }

    private ScrapperConfig scrapperConfig() {
        return new ScrapperConfig(List.of("[href]"));
    }

    public Set<String> chart() {
        Set<String> visited = new HashSet<>();
        Set<String> pending = new HashSet<>();
        pending.add(url);

        while (!pending.isEmpty()) {
            String current = pending.iterator().next();
            pending.remove(current);
            visited.add(current);

            for (String link : chart(current)) {
                if (link != null &&
                    link.startsWith("https://" + host) &&
                    !visited.contains(link) &&
                    !pending.contains(link)) {
                    pending.add(link);
                }
            }
        }

        return visited;
    }


    private Set<String> chart(String url) {
        Scrapper scrapper = scrapper(url);
        Set<String> result = new HashSet<>();

        try {
            for (Object value : scrapper.scrap().getAll().values()) {
                result.add((String) value);
            }
        } catch (SocketTimeoutException e) {
            scrapper.log("Timeout exception accessing '"+scrapper.getUrl()+"'.");
        } catch (HttpStatusException e) {
            scrapper.log("Http status exception accessing '"+scrapper.getUrl()+"'.");
        } catch (MalformedURLException e) {
            scrapper.log("Url '"+scrapper.getUrl()+"' is malformed.");
        } catch (UnsupportedMimeTypeException e) {
            scrapper.log("Unsuported mime type exception accessing '"+scrapper.getUrl()+"'.");
        } catch (IOException e) {
            scrapper.log("General IO exception accessing '"+scrapper.getUrl()+"'.");
            //e.printStackTrace();
        }

        return result;
    }

}
