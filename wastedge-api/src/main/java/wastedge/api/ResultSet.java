package wastedge.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultSet {
    private final List<Object[]> rows;
    private Object[] row;
    private int offset;
    private final List<EntityTypedField> fields = new ArrayList<>();
    private final TObjectIntMap<String> fieldsByName = new TObjectIntHashMap<>();
    private final TObjectIntMap<EntityTypedField> fieldsByField = new TObjectIntHashMap<>();
    private EntitySchema entity;
    private final boolean hasMore;
    private final String nextResult;

    public Object get(int index) {
        return row[index];
    }

    public Object get(String index) {
        Validate.notNull(index, "index");

        return row[fieldsByName.get(index)];
    }

    public Object get(EntityTypedField index) {
        Validate.notNull(index, "index");

        return row[fieldsByField.get(index)];
    }

    public EntitySchema getEntity() {
        return entity;
    }

    public int getFieldCount() {
        return fields.size();
    }

    public int getRowCount() {
        return rows.size();
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public String getNextResult() {
        return nextResult;
    }

    ResultSet(EntitySchema entity,  JsonObject results) {
        Validate.notNull(entity, "entity");
        Validate.notNull(results, "results");

        this.entity = entity;

        reset();

        hasMore = results.get("has_more").getAsBoolean();
        nextResult = results.has("next_result") && !results.get("next_result").isJsonNull() ? results.get("next_result").getAsString() : null;

        JsonArray resultArray = results.getAsJsonArray("result");
        JsonArray headers = resultArray.get(0).getAsJsonArray();

        for (JsonElement header : headers) {
            fields.add((EntityTypedField)entity.getMembers().get(header.getAsString()));
        }
        for (int i = 0; i < fields.size(); i++) {
            EntityTypedField field = fields.get(i);
            fieldsByName.put(field.getName(), i);
            fieldsByField.put(field, i);
        }

        rows = new ArrayList<>(resultArray.size() - 1);

        for (int i = 1; i < resultArray.size(); i++) {
            JsonArray resultRow = resultArray.get(i).getAsJsonArray();
            Object[] row = new Object[resultRow.size()];
            this.rows.add(row);

            for (int j = 0; j < resultRow.size(); j++) {
                Object value = ApiUtils.fromJson(resultRow.get(j));
                switch (fields.get(j).getDataType()) {
                    case DATE:
                        value = ApiUtils.parseDate((String)value);
                        break;
                    case DATE_TIME:
                        value = ApiUtils.parseDateTime((String)value);
                        break;
                    case DATE_TIME_TZ:
                        value = ApiUtils.parseDateTimeOffset((String)value);
                        break;
                }
                row[j] = value;
            }

        }
    }

    public void reset() {
        offset = -1;
    }

    public boolean next() {
        if (offset + 1 < rows.size()) {
            row = rows.get(++offset);
            return true;
        }

        row = null;
        return false;
    }

    public boolean isNull(int index) {
        return get(index) == null;
    }

    public EntityTypedField getField(int index) {
        return fields.get(index);
    }

    public String getFieldName(int index) {
        return fields.get(index).getName();
    }

    public byte[] getBytes(int index) {
        String value = getString(index);
        if (value == null) {
            return null;
        }
        return Base64.decodeBase64(value);
    }

    public String getString(int index) {
        return (String)get(index);
    }

    public LocalDateTime getDateTime(int index) {
        return (LocalDateTime)get(index);
    }

    public DateTime getDateTimeOffset(int index) {
        return (DateTime)get(index);
    }

    public BigDecimal getDecimal(int index) {
        Object value = get(index);
        if (value instanceof Long) {
            return BigDecimal.valueOf((long)value);
        }
        if (value instanceof Double) {
            return BigDecimal.valueOf((double)value);
        }
        return (BigDecimal)value;
    }

    public double getDouble(int index) {
        return ((Number)get(index)).doubleValue();
    }

    public long getLong(int index) {
        return ((Number)get(index)).longValue();
    }

    public boolean getBool(int index) {
        return (Boolean)get(index);
    }
}
