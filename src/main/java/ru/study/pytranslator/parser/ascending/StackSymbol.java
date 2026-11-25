package ru.study.pytranslator.parser.ascending;

import ru.study.pytranslator.lexer.Token;

public class StackSymbol {
    private final String symbol;
    private final Token token;

    public StackSymbol(String symbol, Token token) {
        this.symbol = symbol;
        this.token = token;
    }

    public String getSymbol() {
        return symbol;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
