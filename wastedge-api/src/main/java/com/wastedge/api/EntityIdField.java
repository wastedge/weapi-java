package com.wastedge.api;

public class EntityIdField extends EntityPhysicalField {
    public EntityIdField() {
        super("$id", null, EntityDataType.STRING, -1, true);
    }

    @Override
    public EntityMemberType getType() {
        return EntityMemberType.ID;
    }
}
