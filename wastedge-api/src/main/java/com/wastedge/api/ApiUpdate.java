package com.wastedge.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiUpdate {
    private final Api api;
    private final EntitySchema entity;
    private final ApiUpdateMode mode;
    private final RecordSet records;

    public ApiUpdate(Api api, EntitySchema entity, RecordSet records, ApiUpdateMode mode) {
        Validate.notNull(api, "api");
        Validate.notNull(entity, "entity");

        this.api = api;
        this.entity = entity;
        this.mode = mode;
        if (records == null) {
            this.records = new RecordSet();
        } else {
            this.records = records;
        }
    }

    public EntitySchema getEntity() {
        return entity;
    }

    public ApiUpdateMode getMode() {
        return mode;
    }

    public RecordSet getRecords() {
        return records;
    }

    public List<String> execute() throws IOException {
        String method = mode == ApiUpdateMode.CREATE ? "PUT" : "POST";

        JsonObject response = (JsonObject)api.executeJson(entity.getName(), null, method, records.toJson());

        return buildResponse(response);
    }

    private List<String> buildResponse(JsonObject response) {
        List<String> result = new ArrayList<>();

        for (JsonElement element : response.get("result").getAsJsonArray()) {
            result.add(element.getAsString());
        }

        return result;
    }
}
