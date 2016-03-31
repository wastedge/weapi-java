package com.wastedge.api;

import org.apache.commons.lang3.Validate;

public class QueryOrder {
    private String field;
    private QueryOrderDirection direction;

    public QueryOrder(String field) {
        this(field, QueryOrderDirection.ASCENDING);
    }

    public QueryOrder(String field, QueryOrderDirection direction) {
        Validate.notNull(field, "field");
        Validate.notNull(direction, "direction");

        this.field = field;
        this.direction = direction;
    }

    public String getField() {
        return field;
    }

    public QueryOrderDirection getDirection() {
        return direction;
    }
}
