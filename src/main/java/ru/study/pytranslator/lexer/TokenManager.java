package ru.study.pytranslator.lexer;

import java.util.EnumSet;
import java.util.Map;

import static java.util.Map.entry;

public class TokenManager {
    private TokenManager() { }

    public static final Map<String, TokenType> SPECIAL_WORDS = Map.ofEntries(
            entry("if", TokenType.IF),
            entry("elif", TokenType.ELIF),
            entry("else", TokenType.ELSE),

            entry("and", TokenType.AND),
            entry("or", TokenType.OR),
            entry("not", TokenType.NOT)
    );

    public static final Map<String, TokenType> SPECIAL_SYMBOLS = Map.ofEntries(
            entry(">=", TokenType.GREATEROREQUAL),
            entry("<=", TokenType.LESSOREQUAL),
            entry("==", TokenType.EQUAL),
            entry("!=", TokenType.NOTEQUAL),
            entry(">", TokenType.GREATER),
            entry("<", TokenType.LESS),

            entry("=", TokenType.ASSIGNMENT),
            entry("+", TokenType.PLUS),
            entry("-", TokenType.MINUS),
            entry("/", TokenType.DIVISION),
            entry("*", TokenType.MULTIPLICATION),

            entry(":", TokenType.COLON),
            entry(",", TokenType.COMMA),
            entry("(", TokenType.LPAR),
            entry(")", TokenType.RPAR)
    );
}
