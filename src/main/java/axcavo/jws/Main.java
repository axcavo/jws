package axcavo.jws;

import java.io.IOException;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        Carthographer carthographer = new Carthographer("https://httpbin.org/");
        Set<String> result = carthographer.chart();

        for (String s : result) {
            System.out.println(s);
        }
    }
}