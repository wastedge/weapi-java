package wastedge;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import wastedge.api.Api;
import wastedge.api.ApiCredentials;
import wastedge.api.ApiQuery;
import wastedge.api.QueryOrderDirection;
import wastedge.api.weql.QueryException;
import wastedge.api.weql.WeqlQueryParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
    public void simpleFrom() throws QueryException, IOException {
        ApiQuery query = parse("from system/customer");

        assertEquals("system/customer", query.getEntity().getName());
    }

    @Test
    public void expands() throws QueryException, IOException {
        ApiQuery query = parse("from system/customer expand vendor, services");

        assertEquals("system/customer", query.getEntity().getName());
        assertEquals(2, query.getExpand().size());
        assertEquals("vendor", query.getExpand().get(0));
        assertEquals("services", query.getExpand().get(1));
    }

    @Test
    public void orderBys() throws QueryException, IOException {
        ApiQuery query = parse("from system/customer order by name, start asc, end desc");

        assertEquals("system/customer", query.getEntity().getName());
        assertEquals(3, query.getOrder().size());
        assertEquals("name", query.getOrder().get(0).getField());
        assertEquals(QueryOrderDirection.ASCENDING, query.getOrder().get(0).getDirection());
        assertEquals("start", query.getOrder().get(1).getField());
        assertEquals(QueryOrderDirection.ASCENDING, query.getOrder().get(1).getDirection());
        assertEquals("end", query.getOrder().get(2).getField());
        assertEquals(QueryOrderDirection.DESCENDING, query.getOrder().get(2).getDirection());
    }

    @Test
    public void offset() throws QueryException, IOException {
        ApiQuery query = parse("from system/customer limit 1");

        assertEquals("system/customer", query.getEntity().getName());
        assertEquals(-1, query.getOffset());
        assertEquals(1, query.getCount());
    }

    @Test
    public void offsetAndCount() throws QueryException, IOException {
        ApiQuery query = parse("from system/customer limit 1, 2");

        assertEquals("system/customer", query.getEntity().getName());
        assertEquals(1, query.getOffset());
        assertEquals(2, query.getCount());
    }

    @Test
    public void comparisons() throws QueryException, IOException {
        validateWhere("a = TRUE");
        validateWhere("a = FALSE");
        validateWhere("a = NULL");
        validateWhere("a = b");
        validateWhere("a != b");
        validateWhere("a > b");
        validateWhere("a >= b");
        validateWhere("a = b");
        validateWhere("a < b");
        validateWhere("a <= b");
    }

    @Test
    public void in() throws QueryException, IOException {
        validateWhere("a IN ()");
        validateWhere("a IN (1)");
        validateWhere("a IN ('a')");
        validateWhere("a IN (1, 'a')");
    }

    @Test
    public void methodCall() throws QueryException, IOException {
        validateWhere("a = someMethod()");
        validateWhere("a = someMethod(TRUE)");
        validateWhere("a = someMethod(TRUE, 'a')");
    }

    @Test
    public void logicals() throws QueryException, IOException {
        validateWhere("a = 1 AND b = 1");
        validateWhere("a = 1 OR b = 1");
        validateWhere("a = 1 AND b = 1 OR c = 1");
        validateWhere("(a = 1 OR b = 1) AND c = 1");
    }

    @Test
    public void parameters() throws QueryException, IOException {
        validateWhere("a = 1", "a = :a", buildParameters(":a", 1));
        validateWhere("a = TRUE", "a = :a", buildParameters(":a", true));
        validateWhere("a = NULL", "a = :a", buildParameters(":a", null));
        validateWhere("a = 'abc'", "a = :a", buildParameters(":a", "abc"));
        validateWhere("a = 'a''bc'", "a = :a", buildParameters(":a", "a'bc"));
        validateWhere("a = 7.123", "a = :a", buildParameters(":a", 7.123));
    }

    private Map<String, Object> buildParameters(Object... params) {
        Map<String, Object> parameters = new HashMap<>();

        for (int i = 0; i < params.length / 2; i++) {
            parameters.put((String)params[i * 2], params[i * 2 + 1]);
        }

        return parameters;
    }

    private void validateWhere(String where) throws QueryException, IOException {
        ApiQuery query = parse("from system/customer where " + where);
        assertEquals(where, query.getQuery());
    }

    private void validateWhere(String expected, String where, Map<String, Object> parameters) throws QueryException, IOException {
        ApiQuery query = parse("from system/customer where " + where, parameters);
        assertEquals(expected, query.getQuery());
    }

    private ApiQuery parse(String query) throws QueryException, IOException {
        return parse(query, null);
    }

    private ApiQuery parse(String query, Map<String, Object> parameters) throws QueryException, IOException {
        WeqlQueryParser parser = new WeqlQueryParser();
        return parser.parse(api(), query, parameters);
    }
}
