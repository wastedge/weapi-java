package wastedge.api.jdbc.weql;

public enum BinaryComparisonOperator {
    EQUALS("="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUALS(">="),
    IN("IN"),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUALS("<="),
    LIKE("LIKE"),
    NOT_EQUALS("!="),
    NOT_IN("NOT IN"),
    NOT_LIKE("NOT LIKE");

    private final String code;

    BinaryComparisonOperator(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
