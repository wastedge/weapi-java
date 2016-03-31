package com.wastedge.api.jdbc.weql;

public interface WeqlQueryParameterProvider {
    boolean has(String parameter);

    Object get(String parameter);
}
