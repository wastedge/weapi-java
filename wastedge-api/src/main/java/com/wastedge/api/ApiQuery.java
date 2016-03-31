package com.wastedge.api;

import com.google.gson.JsonObject;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApiQuery {
    private final Api api;
    private final EntitySchema entity;
    private final List<Filter> filters = new ArrayList<>();
    private String query;
    private int offset = -1;
    private int count = -1;
    private String start;
    private final List<String> expand = new ArrayList<>();
    private final List<QueryOrder> order = new ArrayList<>();
    private OutputFormat output = OutputFormat.COMPACT;

    ApiQuery(Api api, EntitySchema entity) {
        Validate.notNull(api, "api");
        Validate.notNull(entity, "entity");

        this.api = api;
        this.entity = entity;
    }

    public EntitySchema getEntity() {
        return entity;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public List<String> getExpand() {
        return expand;
    }

    public List<QueryOrder> getOrder() {
        return order;
    }

    public OutputFormat getOutput() {
        return output;
    }

    public void setOutput(OutputFormat output) {
        this.output = output;
    }

    public RecordSet execute() throws IOException {
        RecordSet result = new RecordSet();

        result.addResultSet(executeReader());

        return result;
    }

    public JsonObject executeJson() throws IOException {
        return (JsonObject)api.executeJson(entity.getName(), buildQueryParameters(), "GET", null);
    }

    public ResultSet executeReader() throws IOException {
        if (output != OutputFormat.COMPACT) {
            throw new ApiException("Output format must be compact");
        }

        return new ResultSet(entity, executeJson());
    }

    private String buildQueryParameters() throws IOException {
        StringBuilder sb = new StringBuilder();

        switch (output) {
            case VERBOSE:
                sb.append("$output=verbose");
                break;
            case COMPACT:
                sb.append("$output=compact");
                break;
            default:
                throw new IllegalArgumentException();
        }

        for (Filter filter : filters) {
            sb.append('&').append(urlEncode(filter.getField().getName())).append('=');

            if (filter.getField() instanceof EntityIdField) {
                if (filter.getType() != FilterType.EQUAL) {
                    throw new ApiException("ID field can only be compared equal");
                }

                append(sb, filter.getValue(), filter.getField().getDataType());
                continue;
            }

            switch (filter.getType()) {
                case IS_NULL:
                    sb.append("is.null");
                    break;
                case NOT_IS_NULL:
                    sb.append("not.is.null");
                    break;
                case IS_TRUE:
                    sb.append("is.true");
                    break;
                case NOT_IS_TRUE:
                    sb.append("not.is.true");
                    break;
                case IS_FALSE:
                    sb.append("is.false");
                    break;
                case NOT_IS_FALSE:
                    sb.append("not.is.false");
                    break;
                case IN:
                    sb.append("in.");
                    appendList(sb, filter.getValue(), filter.getField().getDataType());
                    break;
                case NOT_IN:
                    sb.append("not.in.");
                    appendList(sb, filter.getValue(), filter.getField().getDataType());
                    break;
                case LIKE:
                    sb.append("like.");
                    append(sb, filter.getValue(), filter.getField().getDataType());
                    break;
                case NOT_LIKE:
                    sb.append("not.like.");
                    append(sb, filter.getValue(), filter.getField().getDataType());
                    break;
                case EQUAL:
                    sb.append("eq.");
                    append(sb, filter.getValue(), filter.getField().getDataType());
                    break;
                case NOT_EQUAL:
                    sb.append("not.eq.");
                    append(sb, filter.getValue(), filter.getField().getDataType());
                    break;
                case GREATER_THAN:
                    sb.append("gt.");
                    append(sb, filter.getValue(), filter.getField().getDataType());
                    break;
                case GREATER_EQUAL:
                    sb.append("gte.");
                    append(sb, filter.getValue(), filter.getField().getDataType());
                    break;
                case LESS_THAN:
                    sb.append("lt.");
                    append(sb, filter.getValue(), filter.getField().getDataType());
                    break;
                case LESS_EQUAL:
                    sb.append("lte.");
                    append(sb, filter.getValue(), filter.getField().getDataType());
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        if (order.size() > 0) {
            sb.append("&$order=");
            boolean hadOne = false;
            for (QueryOrder order : this.order) {
                if (hadOne) {
                    sb.append(',');
                } else {
                    hadOne = true;
                }
                sb.append(urlEncode(order.getField())).append(order.getDirection() == QueryOrderDirection.ASCENDING ? ".asc" : ".desc");
            }
        }
        if (expand.size() > 0) {
            sb.append("&$expand=").append(urlEncode(StringUtils.join(expand, ',')));
        }
        if (query != null) {
            sb.append("&$query=").append(urlEncode(query));
        }
        if (offset >= 0) {
            sb.append("&$offset=").append(offset);
        }
        if (count >= 0) {
            sb.append("&$count=").append(count);
        }
        if (start != null) {
            sb.append("&$start=").append(urlEncode(start));
        }

        return sb.toString();
    }

    private void appendList(StringBuilder sb, Object value, EntityDataType dataType) throws IOException {
        if (value == null) {
            return;
        }

        if (!(value instanceof Iterable)) {
            throw new ApiException("Expected parameter to the IN filter to be a collection");
        }

        boolean hadOne = false;

        for (Object element : (Iterable)value) {
            String serialized = ApiUtils.serialize(element, dataType);

            if (serialized.indexOf('\'') != -1 || serialized.indexOf(',') != -1) {
                serialized = "'" + StringUtils.replace(serialized, "'", "''") + "'";
            }

            if (hadOne) {
                sb.append(',');
            } else {
                hadOne = true;
            }

            sb.append(urlEncode(serialized));
        }
    }

    private void append(StringBuilder sb, Object value, EntityDataType dataType) throws IOException {
        sb.append(urlEncode(ApiUtils.serialize(value, dataType)));
    }

    private String urlEncode(String value) throws IOException {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
    }
}
