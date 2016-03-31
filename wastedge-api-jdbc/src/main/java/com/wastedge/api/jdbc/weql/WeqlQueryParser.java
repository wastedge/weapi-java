package com.wastedge.api.jdbc.weql;

import com.wastedge.api.Api;
import com.wastedge.api.ApiQuery;
import com.wastedge.api.QueryOrder;
import com.wastedge.api.QueryOrderDirection;
import com.wastedge.api.jdbc.weql.syntax.LimitClauseSyntax;
import com.wastedge.api.jdbc.weql.syntax.NameSyntax;
import com.wastedge.api.jdbc.weql.syntax.OrderByElementSyntax;
import com.wastedge.api.jdbc.weql.syntax.QuerySyntax;
import org.antlr.runtime.*;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.List;

public class WeqlQueryParser {
    public ApiQuery parse(Api api, String query, List parameters) throws IOException {
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

        ApiQuery apiQuery = api.createQuery(api.getEntitySchema(Printer.print(querySyntax.getFromClause().getFrom())));

        if (querySyntax.getExpandClause() != null) {
            for (NameSyntax nameSyntax : querySyntax.getExpandClause().getElements()) {
                apiQuery.getExpand().add(Printer.print(nameSyntax));
            }
        }

        if (querySyntax.getWhereClause() != null) {
            apiQuery.setQuery(Printer.print(querySyntax.getWhereClause(), parameters));
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
