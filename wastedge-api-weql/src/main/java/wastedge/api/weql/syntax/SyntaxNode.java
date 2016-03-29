package wastedge.api.weql.syntax;

import wastedge.api.weql.syntax.SyntaxActionVisitor;
import wastedge.api.weql.syntax.SyntaxKind;
import wastedge.api.weql.syntax.SyntaxVisitor;
import org.apache.commons.lang3.Validate;
import wastedge.api.weql.Span;

public abstract class SyntaxNode {
    private final Span span;

    protected SyntaxNode(Span span) {
        Validate.notNull(span, "span");

        this.span = span;
    }

    public abstract SyntaxKind getKind();

    public Span getSpan() {
        return span;
    }

    public abstract void accept(SyntaxVisitor visitor) throws Exception;

    public abstract <T> T accept(SyntaxActionVisitor<T> visitor) throws Exception;
}