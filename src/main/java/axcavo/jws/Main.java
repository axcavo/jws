package axcavo.jws;

import java.io.IOException;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Carthographer carthographer = new Carthographer("https://gol.gg/players/list/season-S15/split-Spring/tournament-ALL/");


        long start = System.currentTimeMillis();
        Set<String> result = carthographer.chart();
        long end = System.currentTimeMillis();

        for (String s : result) {
            System.out.println(s);
        }

        System.out.println("Took: " + (end - start) + "ms and found " + result.size() + " unique links.");
    }
}