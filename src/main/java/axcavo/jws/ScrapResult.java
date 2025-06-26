package axcavo.jws;

import java.util.HashMap;
import java.util.Map;

public class ScrapResult {
    private final Map<String, Object> data;

    public ScrapResult() {
        this.data = new HashMap<>();
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public Map<String, Object> getAll() {
        return data;
    }
}
