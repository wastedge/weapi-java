package wastedge.api;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Record {
    private final Map<String, Object> values = new HashMap<>();

    public int size() {
        return values.size();
    }

    public Set<String> getFieldNames() {
        return values.keySet();
    }

    public Object get(String fieldName) {
        Validate.notNull(fieldName, "fieldName");

        return values.get(fieldName);
    }

    public void set(String fieldName, Object value) {
        Validate.notNull(fieldName, "fieldName");

        values.put(fieldName, value);
    }

    public void remove(String fieldName) {
        Validate.notNull(fieldName, "fieldName");

        values.remove(fieldName);
    }

    public boolean containsField(String fieldName) {
        Validate.notNull(fieldName, "fieldName");

        return values.containsKey(fieldName);
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return values.entrySet();
    }

    JsonObject toJson() {
        JsonObject result = new JsonObject();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            result.add(entry.getKey(), ApiUtils.toJson(entry.getValue()));
        }

        return result;
    }
}
