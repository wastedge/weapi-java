package wastedge.api;

import org.apache.commons.lang3.Validate;

public abstract class EntityMember {
    private String name;
    private String comments;

    protected EntityMember(String name, String comments) {
        Validate.notNull(name, "name");

        this.name = name;
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public String getComments() {
        return comments;
    }

    public abstract EntityMemberType getType();

    @Override
    public String toString() {
        return name;
    }
}
