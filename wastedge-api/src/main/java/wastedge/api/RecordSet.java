package wastedge.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;

public class RecordSet extends ArrayList<Record> {
    public RecordSet(int i) {
        super(i);
    }

    public RecordSet() {
    }

    public RecordSet(Collection<? extends Record> collection) {
        super(collection);
    }

    public void addResultSet(ResultSet resultSet) {
        Validate.notNull(resultSet, "resultSet");

        resultSet.reset();

        while (resultSet.next()) {
            Record record = new Record();
            add(record);

            for (int i = 0; i < resultSet.getFieldCount(); i++) {
                record.set(resultSet.getFieldName(i), resultSet.get(i));
            }
        }
    }

    JsonArray toJson() {
        JsonArray array = new JsonArray();

        for (Record record : this) {
            array.add(record.toJson());
        }

        return array;
    }
}
