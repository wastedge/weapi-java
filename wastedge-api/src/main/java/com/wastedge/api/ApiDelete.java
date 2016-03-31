package com.wastedge.api;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiDelete {
    private final Api api;
    private final EntitySchema entity;
    private final List<String> ids;

    ApiDelete(Api api, EntitySchema entity, List<String> ids) {
        Validate.notNull(api, "api");
        Validate.notNull(entity, "entity");

        this.api = api;
        this.entity = entity;
        if (ids == null) {
            this.ids = new ArrayList<>();
        } else {
            this.ids = ids;
        }
    }

    public EntitySchema getEntity() {
        return entity;
    }

    public List<String> getIds() {
        return ids;
    }

    public void execute() throws IOException {
        api.executeJson(buildPath(), null, "DELETE", null);
    }

    private String buildPath() {
        return entity.getName() + "/" + StringUtils.join(ids, ',');
    }
}
