package com.wastedge.api.jdbc.weql;

import com.wastedge.api.jdbc.weql.syntax.*;

import java.text.SimpleDateFormat;
import java.util.*;

class Printer extends AbstractSyntaxVisitor {
    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMAT = new InheritableThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
            simpleDateFormat.setLenient(false);
            return simpleDateFormat;
        }
    };

    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_TZ_FORMAT = new InheritableThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
            simpleDateFormat.setLenient(false);
            return simpleDateFormat;
        }
    };

    private final StringBuilder sb = new StringBuilder();
    private final WeqlQueryParameterProvider parameters;

    public static String print(SyntaxNode node) throws QueryException {
        return print(node, null);
    }

    public static String print(SyntaxNode node, WeqlQueryParameterProvider parameters) throws QueryException {
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

    private Printer(WeqlQueryParameterProvider parameters) {
        this.parameters = parameters;
    }

    private void appendParameter(String parameter) throws QueryException {
        if (parameters == null || !parameters.has(parameter)) {
            throw new QueryException(String.format("Missing parameter '%s'", parameter));
        }

        sb.append(serialize(parameters.get(parameter)));
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
        if (value instanceof Calendar) {
            SimpleDateFormat simpleDateFormat = DATE_TIME_TZ_FORMAT.get();
            Calendar calendar = (Calendar)value;
            simpleDateFormat.setTimeZone(calendar.getTimeZone());
            string = simpleDateFormat.format(calendar.getTime());
        } else if (value instanceof Date) {
            string = DATE_TIME_FORMAT.get().format(value);
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
        appendParameter(syntax.getIdentifier());
    }

    @Override
    public void visitEntityName(EntityNameSyntax syntax) throws Exception {
        syntax.getQualifier().accept(this);
        sb.append('/');
        syntax.getIdentifier().accept(this);
    }

    @Override
    public void visitIdentifier(IdentifierSyntax syntax) throws Exception {
        sb.append(syntax.getName());
    }

    @Override
    public void visitInvocation(InvocationSyntax syntax) throws Exception {
        syntax.getName().accept(this);
        sb.append('(');
        List<ValueSyntax> arguments = syntax.getArguments();
        if (arguments != null) {
            for (int i = 0; i < arguments.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                arguments.get(i).accept(this);
            }
        }
        sb.append(')');
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
        sb.append('(');
        List<ValueSyntax> values = syntax.getValues();
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
