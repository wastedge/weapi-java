package com.wastedge.api.jdbc.weql;

import org.antlr.runtime.*;

public abstract class Parser extends org.antlr.runtime.Parser {
    private String fileName;
    private int nextParameterIndex;

    protected Parser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    protected int getNextParameterIndex() {
        return nextParameterIndex++;
    }

    public int getParameters() {
        return nextParameterIndex;
    }

    @Override
    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) throws RecognitionException {
        throw e;
    }

    @Override
    public void reportError(RecognitionException e) {
        throw new RuntimeException(e);
    }

    protected Span span(Token end) {
        return span(end, end);
    }

    protected Span span(Token start, Token end) {
        return new Span(
            fileName,
            start.getLine(),
            start.getCharPositionInLine(),
            end.getLine(),
            end.getCharPositionInLine() + end.getText().length()
        );
    }

    protected String parseIdentifier(String identifier) {
        if (identifier.length() >= 2 && identifier.charAt(0) == '"') {
            assert identifier.charAt(identifier.length() - 1) == '"';
            return identifier.substring(1, identifier.length() - 1).replace("\"\"", "\"");
        }

        return identifier;
    }
}
