package axcavo.jws;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

public class CartographerFleet {
    private final String baseUrl;
    private final String urlFilter;
    private final int carthographers;
    private final int delay;

    public CartographerFleet(String baseUrl, String urlFilter, int carthographers, int delay) {
        this.baseUrl = baseUrl;
        this.urlFilter = urlFilter;
        this.carthographers = carthographers;
        this.delay = delay;
    }

    public ScrapResult chart() throws InterruptedException {
        Set<String> visited = ConcurrentHashMap.newKeySet();
        Queue<String> pending = new ConcurrentLinkedDeque<>();
        
        ExecutorService executor = Executors.newFixedThreadPool(carthographers);
        CountDownLatch latch = new CountDownLatch(carthographers);

        Cartographer worker = new Cartographer(baseUrl, urlFilter);

        visited.add(baseUrl);
        pending.add(baseUrl);

        for (int i = 0; i < carthographers; i++) {
            executor.submit(createCrawlTask(worker, visited, pending, latch));
        }

        latch.await();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        ScrapResult scrapResult = new ScrapResult();
        scrapResult.put("baseUrl", baseUrl);
        scrapResult.put("urlFilter", urlFilter);
        scrapResult.put("cartographers", carthographers);
        scrapResult.put("delay", delay);
        scrapResult.put("data", visited);

        return scrapResult;
    }

    private Runnable createCrawlTask(Cartographer worker, Set<String> visited, Queue<String> pending, CountDownLatch latch) {
        return () -> {
            try {
                while (true) {
                    String current = pending.poll();
                    if (current == null) {
                        Thread.sleep(delay);
                        current = pending.poll();
                        if (current == null) break;
                    }

                    for (String link : worker.chart(current)) {
                        if (worker.isInScope(link) && visited.add(link)) {
                            pending.add(link);
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        };
    }

}
