package com.wastedge.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Schema {
    private List<String> entities;

    Schema(JsonObject schema) {
        Validate.notNull(schema, "schema");

        List<String> entities = new ArrayList<>();

        for (JsonElement element : schema.get("entities").getAsJsonArray()) {
            entities.add(element.getAsString());
        }

        this.entities = Collections.unmodifiableList(entities);
    }

    public List<String> getEntities() {
        return entities;
    }
}
