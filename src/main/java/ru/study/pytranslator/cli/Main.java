package ru.study.pytranslator.cli;

import ru.study.pytranslator.lexer.Lexer;
import ru.study.pytranslator.lexer.LexerException;
import ru.study.pytranslator.lexer.Token;
import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Path path = Path.of("D:\\projects\\Java\\ТАИФЯ\\python-subset-translator-coursework\\src\\main\\resources\\examples\\basic_example");

        try{
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.tokenize(path);

            System.out.println("_________________________");
            for (Token token : tokens) {
                System.out.println(token);
            }
        } catch (IOException e) {
            System.err.println("Reading error: " + e.getMessage());
        } catch (LexerException e){
            System.err.println(e.getMessage());
        }
    }
}
