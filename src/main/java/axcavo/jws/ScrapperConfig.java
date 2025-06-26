package axcavo.jws;

import java.util.List;

public class ScrapperConfig {
    private final String[] cssQueries;

    public ScrapperConfig(List<String> targets)  {
        this.cssQueries = targets.toArray(new String[targets.size()]);
    }

    public String[] getCssQueries() {
        return cssQueries;
    }
}
