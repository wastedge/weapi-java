package wastedge.api.weql;

import wastedge.api.ApiException;

public class QueryException extends ApiException {
    private final Span span;

    public QueryException() {
        span = null;
    }

    public QueryException(String message) {
        super(message);

        span = null;
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);

        span = null;
    }

    public QueryException(String message, Span span) {
        super(rewriteMessage(message, span));

        this.span = span;
    }

    public QueryException(String message, Throwable cause, Span span) {
        super(rewriteMessage(message, span), cause);

        this.span = span;
    }

    public Span getSpan() {
        return span;
    }

    private static String rewriteMessage(String message, Span span) {
        StringBuilder sb = new StringBuilder();

        if (span.getStartLine() != -1 && span.getStartColumn() != -1) {
            sb.append('(');
            sb.append(span.getStartLine());
            sb.append(',');
            sb.append(span.getStartColumn() + 1);
            if (span.getEndLine() != -1 && span.getEndColumn() != -1 && (span.getStartLine() != span.getEndLine() || span.getStartColumn() != span.getEndColumn())) {
                sb.append(',');
                sb.append(span.getEndLine());
                sb.append(',');
                sb.append(span.getEndColumn() + 1);
            }
            sb.append(')');
        }

        sb.append(' ');
        sb.append(message);

        return sb.toString();
    }
}
