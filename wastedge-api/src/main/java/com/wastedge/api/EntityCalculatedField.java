package com.wastedge.api;

public class EntityCalculatedField extends EntityTypedField {
    public EntityCalculatedField(String name, String comments, EntityDataType dataType, int decimals) {
        super(name, comments, dataType, decimals);
    }

    @Override
    public EntityMemberType getType() {
        return EntityMemberType.CALCUALTED;
    }
}
