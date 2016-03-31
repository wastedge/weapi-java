package com.wastedge.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

public class ApiRowErrors {
    private int row;
    private List<ApiError> errors;

    public ApiRowErrors(int row) {
        this(row, new ArrayList<ApiError>());
    }

    public ApiRowErrors(int row, List<ApiError> errors) {
        this.row = row;
        this.errors = errors;
    }

    static ApiRowErrors fromJson(JsonObject row) {
        Validate.notNull(row, "row");

        List<ApiError> errors = new ArrayList<>();

        for (JsonElement error : row.get("errors").getAsJsonArray()) {
            errors.add(ApiError.fromJson(error.getAsJsonObject()));
        }

        return new ApiRowErrors(
            row.get("row").getAsInt(),
            errors
        );
    }
}
