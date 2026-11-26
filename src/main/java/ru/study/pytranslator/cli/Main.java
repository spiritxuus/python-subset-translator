package ru.study.pytranslator.cli;

import ru.study.pytranslator.lexer.Lexer;
import ru.study.pytranslator.lexer.LexerException;
import ru.study.pytranslator.lexer.Token;
import ru.study.pytranslator.parser.ParseException;
import ru.study.pytranslator.parser.ascending.AscendingParser;
import ru.study.pytranslator.parser.descending.DescendingParser;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Path path = Path.of("D:\\projects\\Java\\ТАИФЯ\\python-subset-translator-coursework\\src\\main\\resources\\examples\\main_example");

        try{
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.tokenize(path);
            DescendingParser descendingParser = new DescendingParser(tokens);
            AscendingParser ascendingParser = new AscendingParser(tokens);

            System.out.println("_____________Lexer____________");
            for (Token token : tokens) {
                System.out.println(token);
            }

            descendingParser.parse();
            System.out.println("Cинтаксических ошибок не найдено (нисходящий анализатор).\n");
            ascendingParser.parse();
            System.out.println("Cинтаксических ошибок не найдено (восходящий анализатор).\n");
        } catch (IOException e) {
            System.err.println("Reading error: " + e.getMessage());
        } catch (LexerException e){
            System.err.println(e.getMessage());
        } catch (ParseException e){
            System.err.println(e.getMessage());
        }
    }
}
