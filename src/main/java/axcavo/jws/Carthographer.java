package axcavo.jws;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;

public class Carthographer {
    private final String url;
    private final String host;

    public Carthographer(String url) {
        this.url = url;
        this.host = getHost(url);
        System.out.println(host);
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

    public Set<String> chart() throws InterruptedException {
        Set<String> visited = ConcurrentHashMap.newKeySet();
        Queue<String> pending = new ConcurrentLinkedDeque<>();

        ExecutorService executor = Executors.newFixedThreadPool(5);

        pending.add(url);
        visited.add(url);

        CountDownLatch latch = new CountDownLatch(1);

        Runnable crawlTask = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);

                    String current;
                    while ((current = pending.poll()) != null) {
                        Set<String> found = chart(current);
                        for (String link : found) {
                            if (link != null &&
                                link.startsWith("https://" + host + "/players/list/season") &&
                                visited.add(link)) {
                                pending.add(link);
                                executor.submit(this);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        };

        executor.submit(crawlTask);
        latch.await();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

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
