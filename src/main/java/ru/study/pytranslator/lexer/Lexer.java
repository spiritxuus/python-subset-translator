package ru.study.pytranslator.lexer;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Lexer {
    private static final int INDENT_WIDTH = 4;
    private static final int MAX_IDENTIFIER_LENGTH = 8; //TODO: подумать

    public List<Token> tokenize(Path path) throws IOException, LexerException {
        List<Token> tokens = new ArrayList<Token>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))){
            String line;
            int lineNumber = 0;

            Deque<Integer> indentStack = new ArrayDeque<>();
            indentStack.push(0);

            while ((line = reader.readLine()) != null){
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                int spaceCount = 0;
                while (spaceCount < line.length()) {
                    char ch = line.charAt(spaceCount);

                    if (ch == ' ') {
                        spaceCount++;
                    }
                    else if (ch == '\t') {
                        throw new LexerException(
                                "Использование табуляции в отступе запрещено. " +
                                        "Используйте пробелы.", lineNumber, spaceCount + 1
                        );
                    }
                    else {
                        break;
                    }
                }

                if (spaceCount % 4 != 0){
                    throw new LexerException(
                            "Некорректный отступ: обнаружено " + spaceCount + " пробелов. " +
                                    "Отступ должен быть кратен " + INDENT_WIDTH + ".", lineNumber, 1
                    );
                }

                int previousIndent = indentStack.peek();
                if (spaceCount > previousIndent){
                    indentStack.push(spaceCount);
                    Token token = new Token(TokenType.INDENT, "INDENT", lineNumber, spaceCount + 1);
                    tokens.add(token);
                }
                else if (spaceCount <= previousIndent){
                    if (!indentStack.contains(spaceCount)) {

                        throw new LexerException(
                                "Несогласованный отступ: " + spaceCount + " пробелов. " +
                                        "Допустимые уровни: " + indentStack, lineNumber, 1
                        );
                    }

                    while (indentStack.peek() > spaceCount){
                        indentStack.pop();
                        Token token = new Token(TokenType.DEDENT, "DEDENT", lineNumber, spaceCount + 1);
                        tokens.add(token);
                    }
                }

                String code = line.substring(spaceCount);
                tokenizeLine(code, tokens, lineNumber, spaceCount);

                tokens.add(new Token(TokenType.NEWLINE, "\n", lineNumber, line.length() + 1));
            }

            while (indentStack.size() > 1) {
                indentStack.pop();
                tokens.add(new Token(TokenType.DEDENT, "DEDENT", lineNumber + 1, 1));
            }
        }
        return tokens;
    }

    private void tokenizeLine(String source, List<Token> tokens, int lineNumber, int columnOffset) throws LexerException {
        int index = 0;

        while (index < source.length()){
            char ch = source.charAt(index);

            if (Character.isWhitespace(ch)) {
                index++;
                continue;
            }

            if (ch >= '0' && ch <= '9') {
                int start = index;

                while (index < source.length()) {
                    char current = source.charAt(index);

                    if (current >= '0' && current <= '9') {
                        index++;
                    }
                    else {
                        break;
                    }
                }

                String number = source.substring(start, index);
                tokens.add(
                        new Token(TokenType.LITERAL, number, lineNumber, columnOffset + start + 1)
                );
                continue;
            }

            if (Character.isLetter(ch) || ch == '_') {
                int start = index;
                index++;

                while (index < source.length()) {
                    char current = source.charAt(index);

                    if (Character.isLetterOrDigit(current) || current == '_') {

                        index++;
                    }
                    else {
                        break;
                    }
                }

                String identifier = source.substring(start, index);
                if (identifier.length() > MAX_IDENTIFIER_LENGTH) {

                    throw new LexerException(
                            "Идентификатор '" + identifier +
                                    "' превышает допустимую длину " + MAX_IDENTIFIER_LENGTH +
                                    " символов.", lineNumber, columnOffset + start + 1
                    );
                }

                TokenType type = TokenManager.SPECIAL_WORDS.getOrDefault(
                        identifier,
                        TokenType.IDENTIFIER
                );

                tokens.add(new Token(type, identifier, lineNumber, columnOffset + start + 1));
                continue;
            }

            if (index + 1 < source.length()) {
                String two = source.substring(index, index + 2);

                if (TokenManager.SPECIAL_SYMBOLS.containsKey(two)) {
                    TokenType type = TokenManager.SPECIAL_SYMBOLS.get(two);
                    tokens.add(new Token(type, two, lineNumber, columnOffset + index + 1));
                    index += 2;
                    continue;
                }
            }

            String one = String.valueOf(ch);
            if (TokenManager.SPECIAL_SYMBOLS.containsKey(one)) {
                TokenType type = TokenManager.SPECIAL_SYMBOLS.get(one);
                tokens.add(new Token(type, one, lineNumber, columnOffset + index + 1));
                index++;
            }
            else {
                throw new LexerException(
                        "Недопустимый символ '" + ch + "'.",
                        lineNumber, columnOffset + index + 1
                );
            }
        }
    }
}
