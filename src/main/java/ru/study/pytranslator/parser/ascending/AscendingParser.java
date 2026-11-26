package ru.study.pytranslator.parser.ascending;

import ru.study.pytranslator.lexer.Token;
import ru.study.pytranslator.lexer.TokenType;
import ru.study.pytranslator.parser.ParseException;
import java.util.*;

//TODO: подробные тесты на нескольких вариантах, состояние 33 or, блоки elif else
public class AscendingParser {
    private final List<Token> tokens;
    private int nextToken;
    private int state;
    private final Deque<StackSymbol> lexemeStack = new ArrayDeque<>();
    private final Deque<Integer> stateStack = new ArrayDeque<>();
    private final Deque<Integer> indentStack = new ArrayDeque<>();
    private boolean isAccepted;

    //debug
    private boolean debug = false;

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

            executeState();
        }
    }

    private void executeState() throws ParseException {
        //TODO: debug
        if (debug){
            System.out.println(
                    "State: " + state +
                            " | Stack: " + lexemeStack +
                            " | State stack: " + stateStack +
                            " | Input: " + currentInput()
            );
        }


        switch (state) {
            case 0: state0(); break;
            case 1: state1(); break;
            case 2: state2(); break;
            case 3: state3(); break;
            case 4: state4(); break;
            case 5: state5(); break;
            case 6: state6(); break;
            case 7: state7(); break;
            case 8: state8(); break;
            case 9: state9(); break;
            case 10: state10(); break;
            case 11: state11(); break;
            case 12: state12(); break;
            case 13: state13(); break;
            case 14: state14(); break;
            case 15: state15(); break;
            case 16: state16(); break;
            case 17: state17(); break;
            case 18: state18(); break;
            case 19: state19(); break;
            case 20: state20(); break;
            case 21: state21(); break;
            case 22: state22(); break;
            case 23: state23(); break;
            case 24: state24(); break;
            case 25: state25(); break;
            case 26: state26(); break;
            case 27: state27(); break;
            case 28: state28(); break;
            case 29: state29(); break;
            case 30: state30(); break;
            case 31: state31(); break;
            case 32: state32(); break;
            case 33: state33(); break;
            case 34: state34(); break;
            case 35: state35(); break;
            case 36: state36(); break;
            case 37: state37(); break;
            case 38: state38(); break;
            case 39: state39(); break;
            case 40: state40(); break;
            case 41: state41(); break;
            case 42: state42(); break;
            case 43: state43(); break;
            case 44: state44(); break;
            case 45: state45(); break;
            case 46: state46(); break;
            case 47: state47(); break;
            case 48: state48(); break;
            case 49: state49(); break;
            case 50: state50(); break;
            case 51: state51(); break;
            case 52: state52(); break;
            case 53: state53(); break;
            case 54: state54(); break;
            case 55: state55(); break;
            case 56: state56(); break;
            case 57: state57(); break;
            case 58: state58(); break;
            default:
                throw new IllegalStateException("Неизвестное состояние автомата: " + state);
        }
    }

    private void shift(TokenType... expected) throws ParseException {
        Token token = currentInput();
        if (token == null) {
            throw error(Arrays.toString(expected));
        }

        boolean matches = false;
        for (TokenType type : expected) {
            if (token.getType() == type) {
                matches = true;
                break;
            }
        }

        if (!matches) {
            throw error(Arrays.toString(expected));
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

    private Token peekInput(int offset) {
        int position = nextToken + offset;

        if (position < 0 || position >= tokens.size()) {
            return null;
        }

        return tokens.get(position);
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

    private boolean isArithmeticExpressionFollow(Token token) {
        if (token == null) {
            return true;
        }

        switch (token.getType()) {
            case P:
            case COMMA:
            case LESS:
            case GREATER:
            case LESSOREQUAL:
            case GREATEROREQUAL:
            case EQUAL:
            case NOTEQUAL:
            case AND:
            case OR:
            case COLON:
            case RPAR:
                return true;
            default:
                return false;
        }
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
                goToState(4);
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
                throw error("id, , или =");
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
                throw error("id или ,");
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
                throw error("id, lit или =");
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
                throw error("id или ,");
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
                throw error("id, lit или ,");
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
            case "<elem>":
                Token next = currentInput();
                if (next != null && (
                        next.getType() == TokenType.PLUS ||
                                next.getType() == TokenType.MINUS ||
                                next.getType() == TokenType.DIVISION ||
                                next.getType() == TokenType.MULTIPLICATION)) {
                    shift(TokenType.PLUS, TokenType.MINUS, TokenType.DIVISION, TokenType.MULTIPLICATION);
                    break;
                }

                if (isArithmeticExpressionFollow(next)) {
                    reduce(1, "<арифм_выр>");
                    break;
                }
                throw error("арифметический оператор или допустимый символ завершения выражения");

            case "<арифм_опер>":
                goToState(16);
                break;
            case "+":
                goToState(20);
                break;
            case "-":
                goToState(21);
                break;
            case "/":
                goToState(22);
                break;
            case "*":
                goToState(23);
                break;
            default:
                throw error("<elem> или арифметический оператор");
        }
    }

    private void state16() throws ParseException {
        switch (topSymbol()) {
            case "<арифм_опер>":
                shift(TokenType.IDENTIFIER, TokenType.LITERAL);
                break;
            case "<elem>":
                goToState(17);
                break;
            case "id":
                goToState(18);
                break;
            case "lit":
                goToState(19);
                break;
            default:
                throw error("id или lit");
        }
    }

    private void state17() throws ParseException {
        if ("<elem>".equals(topSymbol())) {
            reduce(3, "<арифм_выр>");
            return;
        }
        throw error("<elem>");
    }

    private void state18() throws ParseException {
        if ("id".equals(topSymbol())) {
            reduce(1, "<elem>");
            return;
        }
        throw error("id");
    }

    private void state19() throws ParseException {
        if ("lit".equals(topSymbol())) {
            reduce(1, "<elem>");
            return;
        }
        throw error("lit");
    }

    private void state20() throws ParseException {
        if ("+".equals(topSymbol())) {
            reduce(1, "<арифм_опер>");
            return;
        }
        throw error("+");
    }

    private void state21() throws ParseException {
        if ("-".equals(topSymbol())) {
            reduce(1, "<арифм_опер>");
            return;
        }
        throw error("-");
    }

    private void state22() throws ParseException {
        if ("/".equals(topSymbol())) {
            reduce(1, "<арифм_опер>");
            return;
        }
        throw error("/");
    }

    private void state23() throws ParseException {
        if ("*".equals(topSymbol())) {
            reduce(1, "<арифм_опер>");
            return;
        }
        throw error("*");
    }

    private void state24() throws ParseException {
        switch (topSymbol()) {
            case "if":
                shift(TokenType.LPAR, TokenType.NOT, TokenType.IDENTIFIER, TokenType.LITERAL);
                break;
            case "<part>":
                goToState(25);
                break;
            case "<лог_выр>":
                goToState(27);
                break;
            case "<лог_и>":
                goToState(32);
                break;
            case "<лог_опер>":
                goToState(35);
                break;
            case "<сравнение>":
                goToState(36);
                break;
            case "(":
                goToState(37);
                break;
            case "not":
                goToState(40);
                break;
            case "<арифм_выр>":
                goToState(42);
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
                throw error("if, (, not, id или lit");
        }
    }

    private void state25() throws ParseException {
        switch (topSymbol()) {
            case "<part>":
                Token next = currentInput();
                if (next == null) {
                    if (indentStack.size() > 1) {
                        popIndentLevel();
                    }

                    reduce(0, "<список_elif>");
                    break;
                }

                if (next.getType() != TokenType.P) {
                    throw error("P или конец файла");
                }

                int nextIndent = currentIndent();
                int stackIndent = indentStack.peek();
                Token nextLineToken = peekInput(1);

                if (nextIndent < stackIndent) {
                    popIndentLevel();
                    reduce(0, "<список_elif>");
                }

                else if (nextLineToken != null && (nextLineToken.getType() == TokenType.ELIF ||
                                        nextLineToken.getType() == TokenType.ELSE)
                ) {
                    shift(TokenType.P);
                }

                else if (nextIndent == stackIndent && nextLineToken != null &&
                                (nextLineToken.getType() == TokenType.IDENTIFIER ||
                                        nextLineToken.getType() == TokenType.IF)
                ) {
                    reduce(0, "<список_elif>");
                }

                else {
                    throw error("elif или else");
                }

                break;
            case "P":
                shift(TokenType.ELIF, TokenType.ELSE);
                break;
            case "<список_elif>":
                goToState(26);
                break;
            case "elif":
                goToState(51);
                break;
            case "else":
                goToState(54);
                break;
            default:
                throw error("<part>, P, elif или else");
        }
    }

    private void state26() throws ParseException {
        if ("<список_elif>".equals(topSymbol())) {
            reduce(3, "<if>");
            return;
        }
        throw error("<список_elif>");
    }

    private void state27() throws ParseException {
        switch (topSymbol()) {
            case "<лог_выр>":
                shift(TokenType.COLON, TokenType.OR);
                break;
            case ":":
                goToState(28);
                break;
            case "or":
                goToState(31);
                break;
            default:
                throw error(": или or");
        }
    }

    private void state28() throws ParseException {
        switch (topSymbol()) {
            case ":":
                shift(TokenType.P);
                break;
            case "P":
                goToState(29);
                break;
            default:
                throw error(": или P");
        }
    }

    private void state29() throws ParseException {
        switch (topSymbol()) {
            case "P":
                shift(TokenType.IDENTIFIER, TokenType.IF);
                break;
            case "<спис_опер>":
                goToState(30);
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

    private void state30() throws ParseException {
        if ("<спис_опер>".equals(topSymbol())) {
            reduce(4, "<part>");
            return;
        }
        throw error("<спис_опер>");
    }

    private void state31() throws ParseException {
        switch (topSymbol()) {
            case "or":
                shift(TokenType.LPAR, TokenType.NOT, TokenType.IDENTIFIER, TokenType.LITERAL);
                break;
            case "<лог_и>":
                goToState(58);
                break;
            case "<лог_опер>":
                goToState(35);
                break;
            case "<сравнение>":
                goToState(36);
                break;
            case "(":
                goToState(37);
                break;
            case "not":
                goToState(40);
                break;
            case "<арифм_выр>":
                goToState(42);
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
                throw error("or, (, not, id или lit");
        }
    }

    private void state32() throws ParseException {
        switch (topSymbol()) {
            case "<лог_и>":
                Token next = currentInput();
                if (next == null) {
                    throw error("and, ), not, :, id или lit");
                }

                if (next.getType() == TokenType.RPAR ||
                        next.getType() == TokenType.OR ||
                        next.getType() == TokenType.COLON ||
                        next.getType() == TokenType.IDENTIFIER ||
                        next.getType() == TokenType.LITERAL) {
                    reduce(1, "<лог_выр>");
                    break;
                }

                if (next.getType() == TokenType.AND) {
                    shift(TokenType.AND);
                    break;
                }

                if (next.getType() != TokenType.P) {
                    throw error("P или конец файла");
                }
                break;
            case "and":
                goToState(33);
                break;
            default:
                throw error("and, or, (, not, id или lit");
        }
    }

    private void state33() throws ParseException {
        switch (topSymbol()) {
            case "and":
                shift(TokenType.LPAR, TokenType.NOT, TokenType.IDENTIFIER, TokenType.LITERAL);
                break;
            case "<лог_опер>":
                goToState(34);
                break;
            case "<сравнение>":
                goToState(36);
                break;
            case "(":
                goToState(37);
                break;
            case "not":
                goToState(40);
                break;
            case "<арифм_выр>":
                goToState(42);
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
                throw error("or, (, not, id или lit");
        }
    }

    private void state34() throws ParseException {
        if ("<лог_опер>".equals(topSymbol())) {
            reduce(3, "<лог_и>");
            return;
        }
        throw error("<лог_опер>");
    }

    private void state35() throws ParseException {
        if ("<лог_опер>".equals(topSymbol())) {
            reduce(1, "<лог_и>");
            return;
        }
        throw error("<лог_опер>");
    }

    private void state36() throws ParseException {
        if ("<сравнение>".equals(topSymbol())) {
            reduce(1, "<лог_опер>");
            return;
        }
        throw error("<сравнение>");
    }

    private void state37() throws ParseException {
        switch (topSymbol()) {
            case "(":
                Token next = currentInput();
                if (next == null) {
                    throw error("логическое выражение внутри скобок");
                }

                if (next.getType() == TokenType.LPAR) {
                    shift(TokenType.LPAR);
                    goToState(37);
                }
                else {
                    shift(TokenType.NOT, TokenType.IDENTIFIER, TokenType.LITERAL);
                }

                break;
            case "<лог_выр>":
                goToState(38);
                break;
            case "<лог_и>":
                goToState(32);
                break;
            case "<лог_опер>":
                goToState(35);
                break;
            case "<сравнение>":
                goToState(36);
                break;
            case "not":
                goToState(40);
                break;
            case "<арифм_выр>":
                goToState(42);
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
                throw error("(, not, id, lit или соответствующий нетерминал");
        }
    }

    private void state38() throws ParseException {
        switch (topSymbol()) {
            case "<лог_выр>":
                shift(TokenType.RPAR, TokenType.OR);
                break;
            case ")":
                goToState(39);
                break;
            case "or":
                goToState(31);
                break;
            default:
                throw error("or или )");
        }
    }

    private void state39() throws ParseException {
        if (")".equals(topSymbol())) {
            reduce(3, "<лог_опер>");
            return;
        }
        throw error(")");
    }

    private void state40() throws ParseException {
        switch (topSymbol()) {
            case "not":
                Token next = currentInput();
                if (next == null) {
                    throw error("not, (, id или lit");
                }

                if (next.getType() == TokenType.NOT) {
                    shift(TokenType.NOT);
                    goToState(40);
                }
                else {
                    shift(TokenType.LPAR, TokenType.IDENTIFIER, TokenType.LITERAL);
                }

                break;
            case "<лог_опер>":
                goToState(41);
                break;
            case "<сравнение>":
                goToState(36);
                break;
            case "(":
                goToState(37);
                break;
            case "<арифм_выр>":
                goToState(42);
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
                throw error("not, (, id или lit");
        }
    }

    private void state41() throws ParseException {
        if ("<лог_опер>".equals(topSymbol())) {
            reduce(2, "<лог_опер>");
            return;
        }
        throw error("<лог_опер>");
    }

    private void state42() throws ParseException {
        switch (topSymbol()) {
            case "<арифм_выр>":
                shift(TokenType.LESS, TokenType.GREATER, TokenType.LESSOREQUAL,
                        TokenType.GREATEROREQUAL,  TokenType.EQUAL,  TokenType.NOTEQUAL);
                break;
            case "<оператор_сравнения>":
                goToState(49);
                break;
            case "<":
                goToState(43);
                break;
            case ">":
                goToState(44);
                break;
            case "<=":
                goToState(45);
                break;
            case ">=":
                goToState(46);
                break;
            case "==":
                goToState(47);
                break;
            case "!=":
                goToState(48);
                break;
            default:
                throw error("< > <= >= == или !=");
        }
    }

    private void state43() throws ParseException {
        if ("<".equals(topSymbol())) {
            reduce(1, "<оператор_сравнения>");
            return;
        }
        throw error("<");
    }

    private void state44() throws ParseException {
        if (">".equals(topSymbol())) {
            reduce(1, "<оператор_сравнения>");
            return;
        }
        throw error(">");
    }

    private void state45() throws ParseException {
        if ("<=".equals(topSymbol())) {
            reduce(1, "<оператор_сравнения>");
            return;
        }
        throw error("<=");
    }

    private void state46() throws ParseException {
        if (">=".equals(topSymbol())) {
            reduce(1, "<оператор_сравнения>");
            return;
        }
        throw error(">=");
    }

    private void state47() throws ParseException {
        if ("==".equals(topSymbol())) {
            reduce(1, "<оператор_сравнения>");
            return;
        }
        throw error("==");
    }

    private void state48() throws ParseException {
        if ("!=".equals(topSymbol())) {
            reduce(1, "<оператор_сравнения>");
            return;
        }
        throw error("!=");
    }

    private void state49() throws ParseException {
        switch (topSymbol()) {
            case "<оператор_сравнения>":
                shift(TokenType.IDENTIFIER, TokenType.LITERAL);
                break;
            case "<арифм_выр>":
                goToState(50);
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
                throw error("id или lit");
        }
    }

    private void state50() throws ParseException {
        if ("<арифм_выр>".equals(topSymbol())) {
            reduce(3, "<сравнение>");
            return;
        }
        throw error("<арифм_выр>");
    }

    private void state51() throws ParseException {
        switch (topSymbol()) {
            case "elif":
                shift(TokenType.LPAR, TokenType.NOT, TokenType.IDENTIFIER, TokenType.LITERAL);
                break;
            case "<part>":
                goToState(52);
                break;
            case "<лог_выр>":
                goToState(27);
                break;
            case "<лог_и>":
                goToState(32);
                break;
            case "<лог_опер>":
                goToState(35);
                break;
            case "<сравнение>":
                goToState(36);
                break;
            case "(":
                goToState(37);
                break;
            case "not":
                goToState(40);
                break;
            case "<арифм_выр>":
                goToState(42);
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
                throw error("or, (, not, id или lit");
        }
    }

    private void state52() throws ParseException {
        switch (topSymbol()) {
            case "<part>":
                Token next = currentInput();
                if (next == null) {
                    if (indentStack.size() > 1) {
                        popIndentLevel();
                    }

                    reduce(0, "<список_elif>");
                    break;
                }

                if (next.getType() != TokenType.P) {
                    throw error("P или конец файла");
                }

                int nextIndent = currentIndent();
                int stackIndent = indentStack.peek();
                Token nextLineToken = peekInput(1);

                if (nextIndent < stackIndent) {
                    popIndentLevel();
                    reduce(0, "<список_elif>");
                }

                else if (nextLineToken != null && (nextLineToken.getType() == TokenType.ELIF ||
                        nextLineToken.getType() == TokenType.ELSE)
                ) {
                    shift(TokenType.P);
                }

                else if (nextIndent == stackIndent && nextLineToken != null &&
                        (nextLineToken.getType() == TokenType.IDENTIFIER ||
                                nextLineToken.getType() == TokenType.IF)
                ) {
                    reduce(0, "<список_elif>");
                }

                else {
                    throw error("elif или else");
                }

                break;
            case "P":
                shift(TokenType.ELIF, TokenType.ELSE);
                break;
            case "<список_elif>":
                goToState(53);
                break;
            case "elif":
                goToState(51);
                break;
            case "else":
                goToState(54);
                break;
            default:
                throw error("P, elif или else");
        }
    }

    private void state53() throws ParseException {
        if ("<список_elif>".equals(topSymbol())) {
            reduce(3, "<список_elif>");
            return;
        }
        throw error("<список_elif>");
    }

    private void state54() throws ParseException {
        switch (topSymbol()) {
            case "else":
                shift(TokenType.COLON);
                break;
            case ":":
                goToState(55);
                break;
            default:
                throw error("else или :");
        }
    }

    private void state55() throws ParseException {
        switch (topSymbol()) {
            case ":":
                shift(TokenType.P);
                break;
            case "P":
                goToState(56);
                break;
            default:
                throw error(": или P");
        }
    }

    private void state56() throws ParseException {
        switch (topSymbol()) {
            case "P":
                shift(TokenType.IDENTIFIER, TokenType.IF);
                break;
            case "<спис_опер>":
                goToState(57);
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

    private void state57() throws ParseException {
        if ("<спис_опер>".equals(topSymbol())) {
            if (currentInput() == null) {
                popIndentLevel();
            }

            reduce(4, "<список_elif>");
            return;
        }

        throw error("<спис_опер>");
    }

    private void state58() throws ParseException {
        switch (topSymbol()) {
            case "<лог_и>":
                Token next = currentInput();
                if (next == null) {
                    throw error("and, or, (, not, id или lit");
                }

                if (next.getType() == TokenType.AND) {
                    shift(TokenType.AND);
                    //shift(TokenType.LPAR, TokenType.RPAR, TokenType.OR, TokenType.COLON, TokenType.AND, TokenType.IDENTIFIER, TokenType.LITERAL);
                }
                else if (next.getType() == TokenType.LPAR ||
                        next.getType() == TokenType.RPAR ||
                        next.getType() == TokenType.OR ||
                        next.getType() == TokenType.COLON ||
                        next.getType() == TokenType.IDENTIFIER ||
                        next.getType() == TokenType.LITERAL) {
                    reduce(3, "<лог_выр>");
                }
                else {
                    throw error("and, or, (, not, id или lit");
                }

                break;
            case "and":
                goToState(33);
                break;
            default:
                throw error("and");
        }
    }
}
