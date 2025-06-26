package axcavo.jws;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CartographerFleet {
    private final String name;
    private final String baseUrl;
    private final String urlFilter;
    private final int carthographers;
    private final int delay;

    public CartographerFleet(String name, String baseUrl, String urlFilter, int carthographers, int delay) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.urlFilter = urlFilter;
        this.carthographers = carthographers;
        this.delay = delay;
    }

    public ScrapResult chart() throws InterruptedException {
        Set<String> visited = ConcurrentHashMap.newKeySet();
        Queue<String> pending = new ConcurrentLinkedDeque<>();
        
        ExecutorService executor = Executors.newFixedThreadPool(carthographers);

        visited.add(baseUrl);
        pending.add(baseUrl);

        AtomicInteger wip = new AtomicInteger(0);
        for (int i = 0; i < carthographers; i++) {
            Cartographer worker = new Cartographer(name+i, baseUrl, urlFilter);
            executor.submit(createCrawlTask(worker, visited, pending, wip));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        return generateResult(visited);
    }

    private Runnable createCrawlTask(Cartographer worker, Set<String> visited, Queue<String> pending, AtomicInteger wip) {
        return () -> {
            while (true) {
                String current = pending.poll();
                if (current == null) {
                    if (pending.isEmpty() && wip.get() == 0) break;
                    continue;
                }

                wip.incrementAndGet();

                for (String link : worker.chart(current)) {
                    if (worker.isInScope(link) && visited.add(link)) {
                        pending.add(link);
                    }
                }

                wip.decrementAndGet();
            }
        };
    }


    private ScrapResult generateResult(Set<String> visited) {
        ScrapResult scrapResult = new ScrapResult();
        scrapResult.put("baseUrl", baseUrl);
        scrapResult.put("urlFilter", urlFilter);
        scrapResult.put("cartographers", carthographers);
        scrapResult.put("delay", delay);
        scrapResult.put("data", visited);

        return scrapResult;
    }

}
