package ru.study.pytranslator.lexer;

public class LexerException extends Exception {

    private int line;
    private int column;

    public LexerException(String message, int line, int column)
    {
        super("Лексическая ошибка (строка " + line +
                ", столбец " + column + "): " + message
        );

        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
