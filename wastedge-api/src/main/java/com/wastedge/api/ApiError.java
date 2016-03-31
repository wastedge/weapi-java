package com.wastedge.api;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.Validate;

public class ApiError {
    private String field;
    private String error;

    public ApiError(String error) {
        this(null, error);
    }

    public ApiError(String field, String error) {
        Validate.notNull(error, "error");

        this.field = field;
        this.error = error;
    }

    public String getField() {
        return field;
    }

    public String getError() {
        return error;
    }

    static ApiError fromJson(JsonObject error) {
        Validate.notNull(error, "error");

        return new ApiError(
            error.has("field") ? error.get("field").getAsString() : null,
            error.get("error").getAsString()
        );
    }
}
