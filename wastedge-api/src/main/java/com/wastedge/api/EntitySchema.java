package com.wastedge.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.Validate;

import java.util.*;

public class EntitySchema {
    private String name;
    private String comments;
    private List<EntityPhysicalField> idField;
    private EntityField keyField;
    private EntityField labelField;
    private boolean canRead;
    private boolean canCreate;
    private boolean canUpdate;
    private boolean canDelete;
    private Map<String, EntityMember> members;

    public EntitySchema(String name, JsonObject schema) {
        Validate.notNull(name, "name");
        Validate.notNull(schema, "schema");

        this.name = name;

        Map<String, EntityMember> members = new HashMap<>();

        members.put("$id", new EntityIdField());

        for (Map.Entry<String, JsonElement> entry : schema.getAsJsonObject("fields").entrySet()) {
            JsonObject field = (JsonObject)entry.getValue();

            int decimals = field.has("decimals") ? field.get("decimals").getAsInt() : -1;

            switch (field.get("type").getAsString()) {
                case "field":
                    members.put(entry.getKey(), new EntityField(
                        entry.getKey(),
                        field.has("comments") && !field.get("comments").isJsonNull() ? field.get("comments").getAsString() : null,
                        parseDataType(field.get("data_type").getAsString()),
                        decimals,
                        field.get("mandatory").getAsBoolean()
                    ));
                    break;

                case "foreign":
                    members.put(entry.getKey(), new EntityForeign(
                        entry.getKey(),
                        field.has("comments") && !field.get("comments").isJsonNull() ? field.get("comments").getAsString() : null,
                        field.get("link_table").getAsString(),
                        parseDataType(field.get("data_type").getAsString()),
                        decimals,
                        field.get("mandatory").getAsBoolean()
                    ));
                    break;

                case "foreign_child":
                    members.put(entry.getKey(), new EntityForeignChild(
                        entry.getKey(),
                        field.has("comments") && !field.get("comments").isJsonNull() ? field.get("comments").getAsString() : null,
                        field.get("link_table").getAsString(),
                        field.get("link_field").getAsString()
                    ));
                    break;

                case "calculated":
                    members.put(entry.getKey(), new EntityCalculatedField(
                        entry.getKey(),
                        field.has("comments") && !field.get("comments").isJsonNull() ? field.get("comments").getAsString() : null,
                        parseDataType(field.get("data_type").getAsString()),
                        decimals
                    ));
                    break;
            }
        }

        this.members = Collections.unmodifiableMap(members);

        List<EntityPhysicalField> idFields = new ArrayList<>();

        JsonElement ids = schema.get("id");
        if (ids instanceof JsonArray) {
            for (JsonElement id : (JsonArray)ids) {
                idFields.add((EntityPhysicalField)members.get(id.getAsString()));
            }
        } else {
            idFields.add((EntityPhysicalField)members.get(ids.getAsString()));
        }

        this.idField = Collections.unmodifiableList(idFields);

        if (schema.has("key") && !schema.get("key").isJsonNull()) {
            keyField = (EntityField)members.get(schema.get("key").getAsString());
        }
        if (schema.has("label") && !schema.get("label").isJsonNull()) {
            labelField = (EntityField)members.get(schema.get("label").getAsString());
        }
        if (schema.has("comments") && !schema.get("comments").isJsonNull()) {
            comments = schema.get("comments").getAsString();
        }

        for (JsonElement action : schema.getAsJsonArray("actions")) {
            switch (action.getAsString()) {
                case "read":
                    canRead = true;
                    break;
                case "create":
                    canCreate = true;
                    break;
                case "update":
                    canUpdate = true;
                    break;
                case "delete":
                    canDelete = true;
                    break;
            }
        }
    }

    private EntityDataType parseDataType(String dataType) {
        switch (dataType) {
            case "bytes":
                return EntityDataType.BYTES;
            case "string":
                return EntityDataType.STRING;
            case "date":
                return EntityDataType.DATE;
            case "datetime":
                return EntityDataType.DATE_TIME;
            case "datetime-tz":
                return EntityDataType.DATE_TIME_TZ;
            case "decimal":
                return EntityDataType.DECIMAL;
            case "long":
                return EntityDataType.LONG;
            case "int":
                return EntityDataType.INT;
            case "bool":
                return EntityDataType.BOOL;
            default:
                throw new IllegalArgumentException("dataType");
        }
    }

    public String getName() {
        return name;
    }

    public String getComments() {
        return comments;
    }

    public List<EntityPhysicalField> getIdField() {
        return idField;
    }

    public EntityField getKeyField() {
        return keyField;
    }

    public EntityField getLabelField() {
        return labelField;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public boolean isCanCreate() {
        return canCreate;
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public Map<String, EntityMember> getMembers() {
        return members;
    }
}
