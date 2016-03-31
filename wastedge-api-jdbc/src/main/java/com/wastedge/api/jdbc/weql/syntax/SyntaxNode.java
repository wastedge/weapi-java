package com.wastedge.api.jdbc.weql.syntax;

import com.wastedge.api.jdbc.weql.syntax.SyntaxActionVisitor;
import com.wastedge.api.jdbc.weql.syntax.SyntaxKind;
import com.wastedge.api.jdbc.weql.syntax.SyntaxVisitor;
import org.apache.commons.lang3.Validate;
import com.wastedge.api.jdbc.weql.Span;

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
