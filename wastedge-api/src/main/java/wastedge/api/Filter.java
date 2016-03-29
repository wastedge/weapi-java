package wastedge.api;

import org.apache.commons.lang3.Validate;

public class Filter {
    private EntityPhysicalField field;
    private FilterType type;
    private Object value;

    public Filter(EntityPhysicalField field, FilterType type, Object value) {
        Validate.notNull(field, "field");

        this.field = field;
        this.type = type;
        this.value = value;
    }

    public EntityPhysicalField getField() {
        return field;
    }

    public FilterType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
