package ru.study.pytranslator.parser.ascending;

import ru.study.pytranslator.lexer.Token;

import java.util.Deque;
import java.util.List;

public class AscendingParser {
    private final List<Token> tokens;
    private Deque<Token> lexemStack;
    private Deque<Integer> stateStack;
    private int state;
    
    public AscendingParser(List<Token> tokens) {
        this.tokens = tokens;
        this.state = 0;
    }
}
