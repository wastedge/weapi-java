package wastedge.api.weql;

public interface WeqlQueryParameterProvider {
    boolean has(String parameter);

    Object get(String parameter);
}
