package com.wastedge.api.jdbc.weql;

import com.wastedge.api.jdbc.weql.syntax.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

class Printer extends AbstractSyntaxVisitor {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter DATE_TIME_TZ_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").withOffsetParsed();

    private final StringBuilder sb = new StringBuilder();
    private final List parameters;

    public static String print(SyntaxNode node) throws QueryException {
        return print(node, null);
    }

    public static String print(SyntaxNode node, List parameters) throws QueryException {
        Printer printer = new Printer(parameters);

        try {
            node.accept(printer);
        } catch (Exception e) {
            if (e instanceof QueryException) {
                throw (QueryException)e;
            }

            throw new QueryException(e.getMessage(), e);
        }

        return printer.toString();
    }

    private Printer(List parameters) {
        this.parameters = parameters;
    }

    private void appendParameter(int index) {
        Object value;

        if (parameters == null || index < 0 || index >= parameters.size()) {
            value = null;
        } else {
            value = parameters.get(index);
        }

        sb.append(serialize(value));
    }

    private String serialize(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Boolean) {
            return (boolean)value ? "TRUE" : "FALSE";
        }

        String string;
        if (value instanceof LocalDateTime) {
            string = DATE_TIME_FORMAT.print((LocalDateTime)value);
        } else if (value instanceof DateTime) {
            string = DATE_TIME_TZ_FORMAT.print((DateTime)value);
        } else {
            string = value.toString();
        }

        StringBuilder sb = new StringBuilder();

        sb.append('\'');
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            switch (c) {
                case '\'':
                    sb.append("\'\'");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        sb.append('\'');

        return sb.toString();
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    @Override
    public void visitBinaryComparison(BinaryComparisonSyntax syntax) throws Exception {
        syntax.getLeft().accept(this);
        sb.append(' ').append(syntax.getOperator().code()).append(' ');
        syntax.getRight().accept(this);
    }

    @Override
    public void visitBinaryPredicate(BinaryPredicateSyntax syntax) throws Exception {
        syntax.getLeft().accept(this);
        sb.append(' ').append(syntax.getOperator().code()).append(' ');
        syntax.getRight().accept(this);
    }

    @Override
    public void visitDynamicParameter(DynamicParameterSyntax syntax) throws Exception {
        appendParameter(syntax.getIndex());
    }

    @Override
    public void visitIdentifier(IdentifierSyntax syntax) throws Exception {
        sb.append(syntax.getName());
    }

    @Override
    public void visitInvocation(InvocationSyntax syntax) throws Exception {
        syntax.getName().accept(this);
        appendValueList(syntax.getArguments());
    }

    @Override
    public void visitLike(LikeSyntax syntax) throws Exception {
        sb.append("LIKE ");
        syntax.getValue().accept(this);
    }

    @Override
    public void visitLiteral(LiteralSyntax syntax) throws Exception {
        if (syntax.getValue() == null) {
            sb.append("NULL");
        } else {
            sb.append(syntax.getValue());
        }
    }

    @Override
    public void visitParenPredicate(ParenPredicateSyntax syntax) throws Exception {
        sb.append('(');
        syntax.getPredicate().accept(this);
        sb.append(')');
    }

    @Override
    public void visitParenValue(ParenValueSyntax syntax) throws Exception {
        sb.append('(');
        syntax.getValue().accept(this);
        sb.append(')');
    }

    @Override
    public void visitQualifiedName(QualifiedNameSyntax syntax) throws Exception {
        syntax.getQualifier().accept(this);
        sb.append('.');
        syntax.getIdentifier().accept(this);
    }

    @Override
    public void visitUnaryPredicate(UnaryPredicateSyntax syntax) throws Exception {
        sb.append(syntax.getOperator().code()).append(' ');
        syntax.getOperand().accept(this);
    }

    @Override
    public void visitValueList(ValueListSyntax syntax) throws Exception {
        appendValueList(syntax.getValues());
    }

    private void appendValueList(List<ValueSyntax> values) throws Exception {
        sb.append('(');
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                values.get(i).accept(this);
            }
        }
        sb.append(')');
    }
}
