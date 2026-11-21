package ru.study.pytranslator.parser;

import ru.study.pytranslator.lexer.Token;

public class ParseException extends Exception{

    public ParseException(String message){
        super(message);
    }

    /*
    private ParseException error(String excepted){
        Token actual = current();

        String found = actual == null
                ? "конец входа"
                : actual.getType() + " (" + actual.getValue() + ")";

        return new ParseException(
                "Позиция: " + index + ". Ожидалось: " + expected + ". Получено: " + found
        );
    }

     */
}
