package axcavo.jws;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        CartographerFleet fleet = new CartographerFleet(
            "GOL",
            "https://gol.gg/champion/list/season-S15/split-Spring/tournament-ALL/", 
            "/champion/list/season", 10, 
            200);

        long start = System.currentTimeMillis();
        ScrapResult result = fleet.chart();
        long end = System.currentTimeMillis();

        System.out.println(result.getAll());

        System.out.println("Took: " + (end - start) + "ms");
    }
}