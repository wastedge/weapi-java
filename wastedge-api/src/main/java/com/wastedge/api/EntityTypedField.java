package com.wastedge.api;

public abstract class EntityTypedField extends EntityMember {
    private EntityDataType dataType;
    private int decimals;

    protected EntityTypedField(String name, String comments, EntityDataType dataType, int decimals) {
        super(name, comments);

        this.dataType = dataType;
        this.decimals = decimals;
    }

    public EntityDataType getDataType() {
        return dataType;
    }

    public int getDecimals() {
        return decimals;
    }
}
