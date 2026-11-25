package ru.study.pytranslator.parser.ascending;

import ru.study.pytranslator.lexer.Token;
import ru.study.pytranslator.lexer.TokenType;
import ru.study.pytranslator.parser.ParseException;
import java.util.*;

public class AscendingParser {
    private final List<Token> tokens;
    private int nextToken;
    private int state;
    private final Deque<StackSymbol> lexemeStack = new ArrayDeque<>();
    private final Deque<Integer> stateStack = new ArrayDeque<>();
    private final Deque<Integer> indentStack = new ArrayDeque<>();
    private boolean isAccepted;

    public AscendingParser(List<Token> tokens) {
        this.tokens = prepareTokens(tokens);
        this.nextToken = 0;
        this.state = 0;
    }

    public void parse() throws ParseException {
        nextToken = 0;
        state = 0;
        isAccepted = false;

        lexemeStack.clear();
        stateStack.clear();
        indentStack.clear();

        stateStack.push(0);
        indentStack.push(0);

        //TODO: под вопросом
        long maxSteps = Math.max(200, tokens.size() * 100L + 100);
        long steps = 0;

        while (!isAccepted){
            if (++steps > maxSteps) {
                throw new IllegalStateException(
                        "Превышено количество шагов автомата. " +
                                "Возможен бесконечный цикл."
                );
            }
        }

        executeState();
    }

    private void executeState() throws ParseException {
        switch (state){
            case 0:
                state0();
                break;
            case 1:
                state1();
                break;
                //TODO: дописать все состояния

            default:
                throw new IllegalStateException(
                        "Состояние " + state + " не реализовано."
                );
        }
    }

    private void shift(TokenType... expected) throws ParseException {
        Token token = currentInput();
        if (token == null) {
            throw error("ожидалось: " + Arrays.toString(expected));
        }

        boolean matches = false;
        for (TokenType type : expected) {
            if (token.getType() == type) {
                matches = true;
                break;
            }
        }

        if (!matches) {
            throw error("ожидалось: " + Arrays.toString(expected));
        }

        if (token.getType() == TokenType.P) {
            updateIndentStack(token);
        }

        lexemeStack.push(new StackSymbol(toGrammarSymbol(token), token));
        nextToken++;
    }

    private void goToState(int nextState){
        stateStack.push(nextState);
        state = nextState;
    }

    private void reduce(int count, String nonterminal) {
        if (count < 0){
            throw new IllegalArgumentException("Количество удаляемых символов не может быть отрицательным.");
        }

        for (int i = 0; i < count; i++){
            if (lexemeStack.isEmpty() || stateStack.size() <= 1) {
                throw new IllegalStateException("Недостаточно элементов в стеках для свёртки.");
            }

            lexemeStack.pop();
            stateStack.pop();
        }

        lexemeStack.push(new StackSymbol(nonterminal, null));
        state = stateStack.peek();
    }

    private Token currentInput(){
        if (nextToken >= tokens.size()) {
            return null;
        }
        return tokens.get(nextToken);
    }

    private String topSymbol() {
        if (lexemeStack.isEmpty()) {
            return null;
        }
        return lexemeStack.peek().getSymbol();
    }

    private String toGrammarSymbol(Token token){
        switch (token.getType()){
            case P:
                return "P";
            case IDENTIFIER:
                return "id";
            case LITERAL:
                return "lit";
            default:
                return token.getValue();
        }
    }

    private void updateIndentStack(Token token) throws ParseException {
        int indent;
        try {
            indent = Integer.parseInt(token.getValue());
        }
        catch (NumberFormatException exception) {
            throw error("некорректное значение отступа P");
        }

        int currentIndent = indentStack.peek();
        if (indent < currentIndent) {
            throw error("сначала необходимо выполнить свёртку и закрыть уровень отступа");
        }

        if (indent > currentIndent) {
            indentStack.push(indent);
        }
    }

    private void popIndentLevel() throws ParseException {
        if (indentStack.size() <= 1) {
            throw error("невозможно удалить базовый уровень отступа");
        }
        indentStack.pop();
    }

    private boolean isIndentGreaterOrEqual() throws ParseException {
        return currentIndent() >= indentStack.peek();
    }

    private int currentIndent() throws ParseException{
        Token token = currentInput();
        if (token == null || token.getType() != TokenType.P) {
            throw error("ожидался маркер P");
        }

        try {
            return Integer.parseInt(token.getValue());
        }
        catch (NumberFormatException exception) {
            throw error("некорректное значение отступа P");
        }
    }

    private ParseException error(String expected) {
        Token actual = currentInput();

        if (actual != null) {
            String found = toGrammarSymbol(actual);

            if (actual.getType() == TokenType.P) {
                found += " (отступ " + actual.getValue() + ")";
            }
            else {
                found += " ('" + actual.getValue() + "')";
            }

            return new ParseException(
                    "Состояние " + state +
                            ": ожидалось " + expected +
                            ", найдено " + found + ".", actual.getLine(), actual.getColumn()
            );
        }

        int line = 1;
        int column = 1;

        if (!tokens.isEmpty()) {
            Token last = tokens.get(tokens.size() - 1);
            line = last.getLine();
            column = last.getColumn() + last.getValue().length();
        }

        return new ParseException(
                "Состояние " + state +
                        ": ожидалось " + expected +
                        ", обнаружен конец файла.", line, column
        );
    }

    private static List<Token> prepareTokens(List<Token> source) {
        List<Token> result = new ArrayList<>();
        boolean lineStart = true;

        for (Token token : source){
            if (token.getType() == TokenType.NEWLINE){
                lineStart = true;
                continue;
            }

            if (token.getType() == TokenType.INDENT || token.getType() == TokenType.DEDENT) {
                continue;
            }

            if (lineStart){
                int indent = token.getColumn() - 1;
                result.add(new Token(TokenType.P, String.valueOf(indent), token.getLine(), 1));
                lineStart = false;
            }
            result.add(token);
        }

        return result;
    }

    private void state0() throws ParseException {
        if (lexemeStack.isEmpty()){
            shift(TokenType.P);
            return;
        }

        switch (topSymbol()) {
            case "P":
                goToState(1);
                break;
            case "<прог>":
                if (currentInput() != null) {
                    throw error("конец файла");
                }
                isAccepted = true;
                break;
            default:
                throw error("P или <прог>");
        }
    }

    private void state1() throws ParseException {
        switch (topSymbol()) {
            case "P":
                shift(TokenType.IDENTIFIER, TokenType.IF);
                break;
            case "<спис_опер>":
                goToState(7);
                break;
            case "<опер>":
                goToState(2);
                break;
            case "<множ>":
                goToState(5);
                break;
            case "<if>":
                goToState(6);
                break;
            case "if":
                goToState(24);
                break;
            case "id":
                goToState(8);
                break;
            default:
                throw error("P, id, if или допустимый нетерминал");
        }
    }

    private void state2() throws ParseException {
        switch (topSymbol()) {
            case "<опер>":
                Token next = currentInput();
                if (next == null) {
                    reduce(1, "<спис_опер>");
                    break;
                }

                if (next.getType() != TokenType.P){
                    throw error("P или конец файла");
                }

                if (isIndentGreaterOrEqual()) {
                    shift(TokenType.P);
                }
                else {
                    popIndentLevel();
                    reduce(1, "<спис_опер>");
                }

                break;
            case "P":
                goToState(3);
                break;
            default:
                throw error("<опер> или P");
        }
    }

    private void state3() throws ParseException {
        switch (topSymbol()) {
            case "P":
                shift(TokenType.IDENTIFIER, TokenType.IF);
                break;
            case "<спис_опер>":
                goToState(7);
                break;
            case "<опер>":
                goToState(2);
                break;
            case "<множ>":
                goToState(5);
                break;
            case "<if>":
                goToState(6);
                break;
            case "if":
                goToState(24);
                break;
            case "id":
                goToState(8);
                break;
            default:
                throw error("P, id, if или допустимый нетерминал");
        }
        //TODO: или state1()?
    }

    private void state4() throws ParseException {
        if ("<спис_опер>".equals(topSymbol())) {
            reduce(3, "<спис_опер>");
            return;
        }
        throw error("<спис_опер>");
    }

    private void state5() throws ParseException {
        if ("<множ>".equals(topSymbol())) {
            reduce(1, "<опер>");
            return;
        }
        throw error("<множ>");
    }

    private void state6() throws ParseException {
        if ("<if>".equals(topSymbol())) {
            reduce(1, "<опер>");
            return;
        }
        throw error("<if>");
    }

    private void state7() throws ParseException {
        if ("<спис_опер>".equals(topSymbol())) {
            reduce(2, "<прог>");
            return;
        }
        throw error("<спис_опер>");
    }

    private void state8() throws ParseException {
        switch (topSymbol()) {
            case "id":
                shift(TokenType.COMMA, TokenType.ASSIGNMENT);
                break;
            case ",":
                goToState(9);
                break;
            case "=":
                goToState(10);
                break;
            default:
                throw error("id, COMMA или ASSIGNMENT");
        }
    }

    private void state9() throws ParseException {
        switch (topSymbol()) {
            case ",":
                shift(TokenType.IDENTIFIER);
                break;
            case "<множ>":
                goToState(11);
                break;
            case "id":
                goToState(8);
                break;
            default:
                throw error("id или COMMA");
        }
    }

    private void state10() throws ParseException {
        switch (topSymbol()) {
            case "=":
                shift(TokenType.IDENTIFIER, TokenType.LITERAL);
                break;
            case "<арифм_выр>":
                goToState(14);
                break;
            case "<elem>":
                goToState(15);
                break;
            case "id":
                goToState(18);
                break;
            case "lit":
                goToState(19);
                break;
            default:
                throw error("id, lit или ASSIGNMENT");
        }
    }

    private void state11() throws ParseException {
        switch (topSymbol()) {
            case "<множ>":
                shift(TokenType.COMMA);
                break;
            case ",":
                goToState(12);
                break;
            default:
                throw error("id или COMMA");
        }
    }

    private void state12() throws ParseException {
        switch (topSymbol()) {
            case ",":
                shift(TokenType.IDENTIFIER, TokenType.LITERAL);
                break;
            case "<арифм_выр>":
                goToState(13);
                break;
            case "<elem>":
                goToState(15);
                break;
            case "id":
                goToState(18);
                break;
            case "lit":
                goToState(19);
                break;
            default:
                throw error("id, lit или COMMA");
        }
    }

    private void state13() throws ParseException {
        if ("<арифм_выр>".equals(topSymbol())) {
            reduce(5, "<множ>");
            return;
        }
        throw error("<арифм_выр>");
    }

    private void state14() throws ParseException {
        if ("<арифм_выр>".equals(topSymbol())) {
            reduce(3, "<множ>");
            return;
        }
        throw error("<арифм_выр>");
    }

    private void state15() throws ParseException {
        switch (topSymbol()) {
            case "<опер>":
                Token next = currentInput();
                if (next == null) {
                    reduce(1, "<спис_опер>");
                    break;
                }

                if (next.getType() != TokenType.P){
                    throw error("P или конец файла");
                }

                if (isIndentGreaterOrEqual()) {
                    shift(TokenType.P);
                }
                else {
                    popIndentLevel();
                    reduce(1, "<спис_опер>");
                }

                break;
            case "P":
                goToState(3);
                break;
            default:
                throw error("<опер> или P");
        }
    }
}
