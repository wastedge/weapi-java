package wastedge.api.weql;

public enum LiteralType {
    NULL(false),
    STRING(false),
    BOOLEAN(false),
    REAL(true);

    private final boolean numeric;

    private LiteralType(boolean numeric) {
        this.numeric = numeric;
    }

    public boolean isNumeric() {
        return numeric;
    }
}
