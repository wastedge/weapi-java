package com.wastedge.api;

import org.apache.commons.lang3.Validate;

public class EntityForeignChild extends EntityMember {
    private String linkTable;
    private String linkField;

    public EntityForeignChild(String name, String comments, String linkTable, String linkField) {
        super(name, comments);

        Validate.notNull(linkTable, "linkTable");
        Validate.notNull(linkField, "linkField");

        this.linkTable = linkTable;
        this.linkField = linkField;
    }

    public String getLinkTable() {
        return linkTable;
    }

    public String getLinkField() {
        return linkField;
    }

    @Override
    public EntityMemberType getType() {
        return EntityMemberType.FOREIGN_CHILD;
    }
}
