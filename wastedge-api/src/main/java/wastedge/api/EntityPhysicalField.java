package wastedge.api;

public abstract class EntityPhysicalField extends EntityTypedField {
    private boolean mandatory;

    protected EntityPhysicalField(String name, String comments, EntityDataType dataType, int decimals, boolean mandatory) {
        super(name, comments, dataType, decimals);
        this.mandatory = mandatory;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
