package wastedge.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class AppTest {
    private static Api api;

    private Api api() {
        if (api == null) {
            api = new Api(new ApiCredentials(
                "http://attiswrk06",
                "wedge2",
                "pieter",
                "Pieter"
            ));
        }

        return api;
    }

    @Test
    public void simpleQueryRecordSet() throws IOException {
        EntitySchema entity = api().getEntitySchema("system/customer");
        ApiQuery query = api().createQuery(entity);

        query.getFilters().add(new Filter((EntityPhysicalField)entity.getMembers().get("name"), FilterType.EQUAL, "COD"));

        RecordSet results = query.execute();

        assertEquals(1, results.size());
        assertEquals("COD", results.get(0).get("name"));
    }

    @Test
    public void simpleQueryResultSet() throws IOException {
        EntitySchema entity = api().getEntitySchema("system/customer");
        ApiQuery query = api().createQuery(entity);

        query.getFilters().add(new Filter((EntityPhysicalField)entity.getMembers().get("name"), FilterType.EQUAL, "COD"));

        ResultSet results = query.executeReader();

        int nameField = -1;
        for (int i = 0; i < results.getFieldCount(); i++) {
            if (results.getFieldName(i).equals("name")) {
                nameField = i;
                break;
            }
        }

        assertTrue(results.next());
        assertEquals("COD", results.getString(nameField));
        assertFalse(results.next());
    }

    @Test
    public void pagingQuery() throws IOException {
        EntitySchema entity = api().getEntitySchema("system/customer");
        ApiQuery query = api().createQuery(entity);

        query.setCount(10);

        ResultSet results = query.executeReader();
        RecordSet records = new RecordSet();
        records.addResultSet(results);

        assertEquals(10, records.size());

        query.setStart(results.getNextResult());

        RecordSet nextRecords = query.execute();

        assertEquals(10, nextRecords.size());

        assertNotEquals(records.get(0).get("name"), nextRecords.get(0).get("name"));
    }

    @Test
    public void expressionQuery() throws IOException {
        EntitySchema entity = api().getEntitySchema("system/customer");
        ApiQuery query = api().createQuery(entity);

        query.setQuery("name = 'COD'");

        RecordSet results = query.execute();

        assertEquals(1, results.size());
        assertEquals("COD", results.get(0).get("name"));
    }

    @Test
    public void createUpdateDelete() throws IOException {
        EntitySchema entity = api().getEntitySchema("system/customer");

        // Create a new customer.

        ApiUpdate create = api().createCreate(entity);
        Record record = new Record();
        record.set("name", "My New Customer");

        create.getRecords().add(record);

        List<String> ids = create.execute();

        assertEquals(1, ids.size());

        // Validate it was created.

        ApiQuery query = api().createQuery(entity);

        query.getFilters().add(new Filter((EntityPhysicalField)entity.getMembers().get("$id"), FilterType.EQUAL, ids.get(0)));

        RecordSet results = query.execute();
        record = results.get(0);

        assertEquals(ids.get(0), record.get("$id"));
        assertEquals("My New Customer", record.get("name"));

        record.set("name", "My Changed Customer");

        // Update it.

        ApiUpdate update = api().createUpdate(entity);
        update.getRecords().add(record);

        ids = update.execute();

        assertEquals(1, ids.size());
        assertEquals(record.get("$id"), ids.get(0));

        // Validate it was updated.

        query = api().createQuery(entity);

        query.getFilters().add(new Filter((EntityPhysicalField)entity.getMembers().get("$id"), FilterType.EQUAL, ids.get(0)));

        results = query.execute();
        record = results.get(0);

        assertEquals(ids.get(0), record.get("$id"));
        assertEquals("My Changed Customer", record.get("name"));

        // Delete it.

        ApiDelete delete = api().createDelete(entity);
        delete.getIds().add((String)record.get("$id"));

        delete.execute();

        // Validate it was deleted.

        query = api().createQuery(entity);

        query.getFilters().add(new Filter((EntityPhysicalField)entity.getMembers().get("$id"), FilterType.EQUAL, ids.get(0)));

        results = query.execute();

        assertEquals(0, results.size());
    }

    @Test
    public void dateTimeSerialization() throws ApiException {
        Calendar calendar = ApiUtils.parseDateTimeOffset("2000-01-01T12:34:56.789+07:00");
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        String printed = ApiUtils.printDateTimeOffset(calendar);
        assertEquals("2000-01-01T05:34:56.789Z", printed);
    }
}
