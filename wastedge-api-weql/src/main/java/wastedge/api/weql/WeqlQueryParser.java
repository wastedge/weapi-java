package wastedge.api.weql;

import org.antlr.runtime.*;
import org.apache.commons.lang3.Validate;
import wastedge.api.Api;
import wastedge.api.ApiQuery;
import wastedge.api.QueryOrder;
import wastedge.api.QueryOrderDirection;
import wastedge.api.weql.syntax.LimitClauseSyntax;
import wastedge.api.weql.syntax.NameSyntax;
import wastedge.api.weql.syntax.OrderByElementSyntax;
import wastedge.api.weql.syntax.QuerySyntax;

import java.io.IOException;
import java.util.Map;

public class WeqlQueryParser {
    public ApiQuery parse(Api api, String query, Map<String, Object> parameters) throws QueryException, IOException {
        Validate.notNull(api, "api");
        Validate.notNull(query, "query");

        WeqlLexer lexer = new WeqlLexer(new CaseInsensitiveStringStream(query));
        WeqlParser parser = new WeqlParser(new CommonTokenStream(lexer));

        QuerySyntax querySyntax;
        try {
            try {
                querySyntax = parser.direct_select_statement_multiple_rows();
            } catch (RuntimeException e) {
                if (e.getCause() instanceof RecognitionException) {
                    throw (RecognitionException)e.getCause();
                }
                if (e.getCause() instanceof QueryException) {
                    throw (QueryException)e.getCause();
                }
                throw e;
            }
        } catch (RecognitionException e) {
            String message;

            if (e instanceof MismatchedTokenException) {
                message = String.format("Unexpected '%s'", e.token.getText());
            } else {
                message = e.toString();
            }

            throw new QueryException(message, e, new Span(null, e.line, e.charPositionInLine));
        }

        ApiQuery apiQuery = api.createQuery(api.getEntitySchema(printName(querySyntax.getFromClause().getFrom())));

        if (querySyntax.getExpandClause() != null) {
            for (NameSyntax nameSyntax : querySyntax.getExpandClause().getElements()) {
                apiQuery.getExpand().add(printName(nameSyntax));
            }
        }

        if (querySyntax.getWhereClause() != null) {
            WherePrinter printer = new WherePrinter(parameters);

            try {
                querySyntax.getWhereClause().accept(printer);
            } catch (Exception e) {
                if (e instanceof QueryException) {
                    throw (QueryException)e;
                }

                throw new QueryException(e.getMessage(), e);
            }

            apiQuery.setQuery(printer.toString());
        }

        if (querySyntax.getOrderByClause() != null) {
            for (OrderByElementSyntax orderByElementSyntax : querySyntax.getOrderByClause().getElements()) {
                apiQuery.getOrder().add(new QueryOrder(
                    orderByElementSyntax.getIdentifier().getName(),
                    orderByElementSyntax.getType() == OrderType.DESCENDING ? QueryOrderDirection.DESCENDING : QueryOrderDirection.ASCENDING
                ));
            }
        }

        LimitClauseSyntax limitClause = querySyntax.getLimitClause();
        if (limitClause != null) {
            if (limitClause.getOffset() != null) {
                apiQuery.setOffset(Integer.parseInt(limitClause.getOffset().getValue()));
            }
            if (limitClause.getRowCount() != null) {
                apiQuery.setCount(Integer.parseInt(limitClause.getRowCount().getValue()));
            }
        }

        return apiQuery;
    }

    private String printName(NameSyntax nameSyntax) {
        NamePrinter printer = new NamePrinter();

        try {
            nameSyntax.accept(printer);
        } catch (Exception e) {
            // Ignore.
        }

        return printer.toString();
    }

    private class CaseInsensitiveStringStream extends ANTLRStringStream {
        public CaseInsensitiveStringStream(String input) {
            super(input);
        }

        @Override
        public int LA(int i) {
            int la = super.LA(i);
            if (la == 0 || la == CharStream.EOF) {
                return la;
            }

            // If we're returning a real character, convert it to upper case. The reason we do this is that this
            // grammar is case insensitive, but the grammar only matches for upper case. By making LA return
            // only upper case characters, we can fake the input being all upper case. We are still returning
            // the correct case from the lexer.
            return Character.toUpperCase(la);
        }
    }
}
