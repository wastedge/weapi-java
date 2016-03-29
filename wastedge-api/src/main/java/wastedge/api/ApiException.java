package wastedge.api;

import java.io.IOException;
import java.util.List;

public class ApiException extends IOException {
    private final List<ApiRowErrors> errors;

    public ApiException() {
        errors = null;
    }

    public ApiException(String s) {
        super(s);

        errors = null;
    }

    public ApiException(String s, Throwable throwable) {
        super(s, throwable);

        errors = null;
    }

    public ApiException(List<ApiRowErrors> errors) {
        this.errors = errors;
    }

    public ApiException(String s, List<ApiRowErrors> errors) {
        super(s);

        this.errors = errors;
    }

    public ApiException(String s, Throwable throwable, List<ApiRowErrors> errors) {
        super(s, throwable);

        this.errors = errors;
    }
}
