package ru.study.pytranslator.lexer;

import java.util.Objects;

public class Token {
    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;

    public Token(TokenType type, String value, int line, int column){
        this.type = Objects.requireNonNull(type, "Token value cannot be null");;
        this.value = Objects.requireNonNull(value, "Token value cannot be null");;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString(){
        String printableValue = value
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        return String.format(
                "%s : %s [%d:%d]", type, printableValue, line, column
        );
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() { return line; }

    public int getColumn() { return column; }
}
