package com.wastedge;

import com.wastedge.api.Api;
import com.wastedge.api.ApiCredentials;
import com.wastedge.api.ApiQuery;
import com.wastedge.api.QueryOrderDirection;
import com.wastedge.api.jdbc.weql.WeqlQueryParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    public void withSelect() throws IOException {
        ApiQuery query = parse("select * from \"system/customer\"");

        assertEquals("system/customer", query.getEntity().getName());
    }

    @Test
    public void simpleFrom() throws IOException {
        ApiQuery query = parse("from \"system/customer\"");

        assertEquals("system/customer", query.getEntity().getName());
    }

    @Test
    public void expands() throws IOException {
        ApiQuery query = parse("from \"system/customer\" expand vendor, services");

        assertEquals("system/customer", query.getEntity().getName());
        assertEquals(2, query.getExpand().size());
        assertEquals("vendor", query.getExpand().get(0));
        assertEquals("services", query.getExpand().get(1));
    }

    @Test
    public void orderBys() throws IOException {
        ApiQuery query = parse("from \"system/customer\" order by name, start asc, end desc");

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
    public void offset() throws IOException {
        ApiQuery query = parse("from \"system/customer\" limit 1");

        assertEquals("system/customer", query.getEntity().getName());
        assertEquals(-1, query.getOffset());
        assertEquals(1, query.getCount());
    }

    @Test
    public void offsetAndCount() throws IOException {
        ApiQuery query = parse("from \"system/customer\" limit 1, 2");

        assertEquals("system/customer", query.getEntity().getName());
        assertEquals(1, query.getOffset());
        assertEquals(2, query.getCount());
    }

    @Test
    public void comparisons() throws IOException {
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
    public void in() throws IOException {
        validateWhere("a IN ()");
        validateWhere("a IN (1)");
        validateWhere("a IN ('a')");
        validateWhere("a IN (1, 'a')");
    }

    @Test
    public void methodCall() throws IOException {
        validateWhere("a = someMethod()");
        validateWhere("a = someMethod(TRUE)");
        validateWhere("a = someMethod(TRUE, 'a')");
    }

    @Test
    public void logicals() throws IOException {
        validateWhere("a = 1 AND b = 1");
        validateWhere("a = 1 OR b = 1");
        validateWhere("a = 1 AND b = 1 OR c = 1");
        validateWhere("(a = 1 OR b = 1) AND c = 1");
    }

    @Test
    public void parameters() throws IOException {
        validateWhere("a = 1", "a = ?",  buildParameters(1));
        validateWhere("a = TRUE", "a = ?", buildParameters(true));
        validateWhere("a = NULL", "a = ?", buildParameters((Object)null));
        validateWhere("a = 'abc'", "a = ?", buildParameters("abc"));
        validateWhere("a = 'a''bc'", "a = ?", buildParameters("a'bc"));
        validateWhere("a = 7.123", "a = ?", buildParameters(7.123));
    }

    private List buildParameters(Object... params) {
        return Arrays.asList(params);
    }

    private void validateWhere(String where) throws IOException {
        ApiQuery query = parse("from \"system/customer\" where " + where);
        assertEquals(where, query.getQuery());
    }

    private void validateWhere(String expected, String where, List parameters) throws IOException {
        ApiQuery query = parse("from \"system/customer\" where " + where, parameters);
        assertEquals(expected, query.getQuery());
    }

    private ApiQuery parse(String query) throws IOException {
        return parse(query, null);
    }

    private ApiQuery parse(String query, List parameters) throws IOException {
        WeqlQueryParser parser = new WeqlQueryParser();
        return parser.parse(api(), query, parameters);
    }
}
