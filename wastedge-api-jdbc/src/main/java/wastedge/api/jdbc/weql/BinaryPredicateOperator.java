package wastedge.api.jdbc.weql;

public enum BinaryPredicateOperator {
    AND("AND"),
    OR("OR");

    private final String code;

    BinaryPredicateOperator(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
