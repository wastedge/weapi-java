package wastedge.api;

public class EntityField extends EntityPhysicalField {
    public EntityField(String name, String comments, EntityDataType dataType, int decimals, boolean mandatory) {
        super(name, comments, dataType, decimals, mandatory);
    }

    @Override
    public EntityMemberType getType() {
        return EntityMemberType.FIELD;
    }
}
