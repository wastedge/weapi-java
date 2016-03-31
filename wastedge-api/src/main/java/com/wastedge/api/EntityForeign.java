package com.wastedge.api;

import org.apache.commons.lang3.Validate;

public class EntityForeign extends EntityPhysicalField {
    private String linkTable;

    public EntityForeign(String name, String comments, String linkTable, EntityDataType dataType, int decimals, boolean mandatory) {
        super(name, comments, dataType, decimals, mandatory);

        Validate.notNull(linkTable, "linkTable");

        this.linkTable = linkTable;
    }

    public String getLinkTable() {
        return linkTable;
    }

    @Override
    public EntityMemberType getType() {
        return EntityMemberType.FOREIGN;
    }
}
