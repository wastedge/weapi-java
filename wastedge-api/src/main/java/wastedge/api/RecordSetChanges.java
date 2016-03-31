package wastedge.api;

import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused"})
public class RecordSetChanges {
    public static RecordSetChanges create(RecordSet original, RecordSet modified) {
        Validate.notNull(original, "original");
        Validate.notNull(modified, "modified");

        Map<String, Record> originalMap = getMap(original);
        Map<String, Record> modifiedMap = getMap(modified);

        List<String> deleted = new ArrayList<>();
        for (String key : originalMap.keySet()) {
            if (!modifiedMap.containsKey(key)) {
                deleted.add(key);
            }
        }

        List<Record> newRecords = new ArrayList<>();
        List<Record> modifiedRecords = new ArrayList<>();

        for (Record modifiedRecord : modified) {
            String id = getId(modifiedRecord);

            Record originalRecord = id == null ? null : originalMap.get(id);
            if (originalRecord != null) {
                if (!areEqual(originalRecord, modifiedRecord)) {
                    modifiedRecords.add(modifiedRecord);
                }
            } else {
                newRecords.add(modifiedRecord);
            }
        }

        return new RecordSetChanges(new RecordSet(newRecords), new RecordSet(modifiedRecords), deleted);
    }

    private static boolean areEqual(Record original, Record modified) {
        for (String fieldName : modified.getFieldNames()) {
            if (!original.containsField(fieldName)) {
                return false;
            }
            if (!areValuesEqual(original.get(fieldName), modified.get(fieldName))) {
                return false;
            }
        }

        return true;
    }

    private static boolean areValuesEqual(Object original, Object modified) {
        if (original != null && original.equals("")) {
            original = null;
        }
        if (modified != null && modified.equals("")) {
            modified = null;
        }

        if (original == null && modified == null) {
            return true;
        }
        if (original == null || modified == null) {
            return false;
        }

        if (original.getClass() == modified.getClass()) {
            return original.equals(modified);
        }

        original = simplifyNumber(original);
        modified = simplifyNumber(modified);

        return original.equals(modified);
    }

    private static Object simplifyNumber(Object value) {
        if (value instanceof Short || value instanceof Integer || value instanceof Long) {
            return BigDecimal.valueOf(((Number)value).longValue());
        }
        if (value instanceof Float || value instanceof Double) {
            return BigDecimal.valueOf(((Number)value).doubleValue());
        }
        return value;
    }

    private static Map<String, Record> getMap(RecordSet recordSet) {
        Map<String, Record> map = new HashMap<>();

        for (Record record : recordSet) {
            String id = getId(record);
            if (id != null) {
                map.put(id, record);
            }
        }

        return map;
    }

    private static String getId(Record record) {
        if (!record.containsField("$id")) {
            return null;
        }

        Object id = record.get("$id");
        if (id == null || id.equals("")) {
            return null;
        }

        return id.toString();
    }

    private RecordSet new_;
    private RecordSet modified;
    private List<String> deleted;

    private RecordSetChanges(RecordSet new_, RecordSet modified, List<String> deleted) {
        this.new_ = new_;
        this.modified = modified;
        this.deleted = deleted;
    }

    public RecordSet getNew() {
        return new_;
    }

    public RecordSet getModified() {
        return modified;
    }

    public List<String> getDeleted() {
        return deleted;
    }
}
