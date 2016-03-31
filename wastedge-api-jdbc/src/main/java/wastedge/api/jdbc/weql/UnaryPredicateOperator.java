package wastedge.api.jdbc.weql;

public enum UnaryPredicateOperator {
    NOT("NOT");

    private final String code;

    UnaryPredicateOperator(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
