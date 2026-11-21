package ru.study.pytranslator.parser.descending;

import ru.study.pytranslator.lexer.Token;
import ru.study.pytranslator.lexer.TokenType;
import ru.study.pytranslator.parser.ParseException;
import java.util.List;


public class DescendingParser {
    private final List<Token> tokens;
    private int index;

    public DescendingParser(List<Token> tokens){
        this.tokens = List.copyOf(tokens);
        this.index = 0;
    }

    public void parse() throws ParseException {
        index = 0;
        program();
    }

    private Token current(){
        if (index >= tokens.size()) {
            return null;
        }

        return tokens.get(index);
    }

    private void advance() {
        index++;
    }

    private Token expect(TokenType expected) throws ParseException {
        Token token = current();

        if (token == null || token.getType() != expected) {
            throw error(String.valueOf(expected));
        }

        advance();
        return token;
    }

    //<прог > ::== P <спис_опер> (id if)
    private void program() throws ParseException {
        operatorList();

        if (current() != null) {
            throw error("конец файла");
        }
    }

    //<спис_опер> ::== <опер> <X> (id if)
    private void operatorList() throws ParseException {
        boolean previousWasIf = operator();
        operatorListTail(previousWasIf);
    }

    /*
        <спис_опер> ::== <опер> <X>    (id if)
        <X> ::== ε                     #
        <X> ::== P <спис_опер>`        (id if)
        <спис_опер >` ::== <опер> <X>  (id if)

        <X> - OperatorListTail
     */
    private void operatorListTail(boolean previousWasIf) throws ParseException {
        Token token = current();

        if (token == null || token.getType() == TokenType.DEDENT) {
            return;
        }

        //Обычное завершение
        if (token.getType() == TokenType.NEWLINE){
            expect(TokenType.NEWLINE);
            token = current();

            if (isOperatorStart(token)){
                operatorList();
                return;
            }

            if (token == null || token.getType() == TokenType.DEDENT){
                return;
            }
            throw error("начало оператора, DEDENT или конец файла");
        }
        //После составного if оператор идет сразу, бе з newline
        if (previousWasIf && isOperatorStart(token)) {
            operatorList();
            return;
        }
        throw error("NEWLINE, DEDENT или конец файла");
    }

    private boolean isOperatorStart(Token token) {
        return token != null &&
                (token.getType() == TokenType.IDENTIFIER ||
                        token.getType() == TokenType.IF);
    }

    /*
        <опер> ::== <множ> (id)
        <опер> ::== <if>   (if)
     */
    private boolean operator() throws ParseException {
        Token token = current();

        if (token == null){
            throw error("IDENTIFIER или IF");
        }

        switch(token.getType()){
            case IDENTIFIER:
                assignment();
                return false;
            case IF:
                ifStatement();
                return true;
            default:
                throw error("IDENTIFIER или IF");
        }
    }

    //<if> ::== if <part> <список_elif> (if)
    private void ifStatement() throws ParseException {
        expect(TokenType.IF);
        part();
        elseIfList();
    }

    //<part> ::== <лог_выр> : P <спис_опер> (id lit ( not)
    private void part() throws ParseException {
        logicalExpression();
        expect(TokenType.COLON);
        expect(TokenType.NEWLINE);
        expect(TokenType.INDENT);
        operatorList();
        expect(TokenType.DEDENT);
    }

    /*
        <список_elif> ::== elif <part> <список_elif>    (elif)
        <список_elif> ::== else : P <спис_опер>         (else)
        <список_elif> ::== ε                            #
     */
    private void elseIfList() throws ParseException {
        Token token = current();

        if (token != null && token.getType() == TokenType.ELIF){
            expect(TokenType.ELIF);
            part();
            elseIfList();
            return;
        }

        if (token != null && token.getType() == TokenType.ELSE){
            expect(TokenType.ELSE);
            expect(TokenType.COLON);
            expect(TokenType.NEWLINE);
            expect(TokenType.INDENT);
            operatorList();
            expect(TokenType.DEDENT);
            return;
        }

        if (token == null ||
                token.getType() == TokenType.IDENTIFIER ||
                token.getType() == TokenType.IF ||
                token.getType() == TokenType.DEDENT) {

            return;
        }
        throw error("ELIF, ELSE или конец условной конструкции");
    }

    //<множ> ::== id = <Y> (id)
    private void assignment() throws ParseException {
        expect(TokenType.IDENTIFIER);
        assignmentTail();
    }

    /*
        <множ> ::== id = <Y>                (id)
        <Y> ::== <арифм_выр>                (=)
        <Y> ::== , <множ>` , <арифм_выр>    (,)
        <множ>` ::== id = <Y>               (id)

        <Y> - AssignmentTail
     */
    private void assignmentTail() throws ParseException {
        Token token = current();

        if (token == null) {
            throw error("ASSIGNMENT или COMMA");
        }

        if (token.getType() == TokenType.ASSIGNMENT) {
            expect(TokenType.ASSIGNMENT);
            arithmeticExpression();
        }
        else if (token.getType() == TokenType.COMMA) {
            expect(TokenType.COMMA);
            assignment();
            expect(TokenType.COMMA);
            arithmeticExpression();
        }
        else {
            throw error("ASSIGNMENT или COMMA");
        }
    }

    //<арифм_выр> ::== <elem> <Z> (id lit)
    private void arithmeticExpression() throws ParseException {
        element();
        arithmeticExpressionTail();
    }

    /*
        <арифм_выр> ::== <elem> <Z>    (id lit)
        <Z> ::== ε                      #
        <Z> ::== <арифм_опер> , <elem> (+-/*)

        <Z> - ArithmeticExpressionTail
     */
    private void arithmeticExpressionTail() throws ParseException {
        Token token = current();

        if (isArithmeticOperator(token)) {
            arithmeticOperator();
            element();
            return;
        }

        if (isArithmeticExpressionFollow(token)) {
            return;
        }

        throw error("арифметический оператор или конец выражения");
    }

    private boolean isArithmeticOperator(Token token) {
        if (token == null) {
            return false;
        }

        switch (token.getType()) {
            case PLUS:
            case MINUS:
            case DIVISION:
            case MULTIPLICATION:
                return true;
            default:
                return false;
        }
    }

    private boolean isArithmeticExpressionFollow(Token token) {
        if (token == null) {
            return true;
        }

        switch (token.getType()) {
            case NEWLINE:
            case COMMA:
            case LESS:
            case GREATER:
            case LESSOREQUAL:
            case GREATEROREQUAL:
            case EQUAL:
            case NOTEQUAL:
            case AND:
            case OR:
            case RPAR:
            case COLON:
                return true;
            default:
                return false;
        }
    }

    /*
        <арифм_опер> ::== + (+)
        <арифм_опер> ::== - (-)
        <арифм_опер> ::== / (/)
        <арифм_опер> ::== * (*)
     */
    private void arithmeticOperator() throws ParseException {
        Token token = current();

        if (token == null) {
            throw error("PLUS, MINUS, DIVISION или MULTIPLICATION");
        }

        switch(token.getType()){
            case PLUS:
                expect(TokenType.PLUS);
                break;
            case MINUS:
                expect(TokenType.MINUS);
                break;
            case DIVISION:
                expect(TokenType.DIVISION);
                break;
            case MULTIPLICATION:
                expect(TokenType.MULTIPLICATION);
                break;
            default:
                throw error("PLUS, MINUS, DIVISION или MULTIPLICATION");
        }
    }

    /*
        <elem> ::== id  (lit)
        <elem> ::== lit (id)
     */
    private void element() throws ParseException {
        Token token = current();

        if (token == null){
            throw error("IDENTIFIER или LITERAL");
        }

        switch(token.getType()){
            case IDENTIFIER:
                expect(TokenType.IDENTIFIER);
                break;
            case LITERAL:
                expect(TokenType.LITERAL);
                break;
            default:
                throw error("IDENTIFIER или LITERAL");
        }
    }

    //<лог_выр> ::== <лог_и> <C> (id lit ( not)
    private void logicalExpression() throws ParseException {
        logicalAnd();
        logicalExpressionTail();
    }

    /*
       <лог_выр> ::== <лог_и> <C>  (id lit ( not)
       <C> ::== ε                  #
       <C> ::== or <лог_выр>`      (or)
       <лог_выр>` ::== <лог_и> <C> (id lit ( not)

       <C> - LogicalExpressionTail
    */
    private void logicalExpressionTail() throws ParseException {
        Token token = current();

        if (token != null && token.getType() == TokenType.OR) {
            expect(TokenType.OR);
            logicalAnd();
            logicalExpressionTail();
            return;
        }

        if (token != null &&
                (token.getType() == TokenType.COLON ||
                        token.getType() == TokenType.RPAR)) {
            return;
        }

        throw error("OR, COLON или RPAR");
    }

    //<лог_и> ::== <лог_опер> <V> (id lit ( not)
    private void logicalAnd() throws ParseException {
        logicalOperator();
        logicalAndTail();
    }

    /*
        <лог_и> ::== <лог_опер> <V>  (id lit ( not)
        <V> ::== ε                   #
        <V> ::== and <лог_и>`         (and)
        <лог_и>` ::== <лог_опер> <V> (id lit ( not)

        <V> - LogicalAndTail
     */
    private void logicalAndTail() throws ParseException {
        Token token = current();

        if (token != null && token.getType() == TokenType.AND) {
            expect(TokenType.AND);
            logicalOperator();
            logicalAndTail();
            return;
        }

        if (token != null &&
                (token.getType() == TokenType.OR ||
                        token.getType() == TokenType.COLON ||
                        token.getType() == TokenType.RPAR)) {

            return;
        }

        throw error("AND, OR, COLON или RPAR");
    }

    /*
        <лог_опер> ::== <сравнение> (id lit)
        <лог_опер> ::== (<лог_выр>)     (()
        <лог_опер> ::== not <лог_опер> (not)
     */
    private void logicalOperator() throws ParseException {
        Token token = current();

        if (token == null) {
            throw error("NOT, LPAR, IDENTIFIER или LITERAL");
        }

        switch (token.getType()) {
            case NOT:
                expect(TokenType.NOT);
                logicalOperator();
                break;
            case LPAR:
                expect(TokenType.LPAR);
                logicalExpression();
                expect(TokenType.RPAR);
                break;
            case IDENTIFIER:
            case LITERAL:
                comparison();
                break;
            default:
                throw error("NOT, LPAR, IDENTIFIER или LITERAL");
        }
    }

    //<сравнение> ::== <арифм_выр> <оператор_сравнения> <арифм_выр> (id lit)
    private void comparison() throws ParseException {
        arithmeticExpression();
        comparisonOperator();
        arithmeticExpression();
    }

    /*
        <оператор_сравнения> ::== < (<)
        <оператор_сравнения> ::== > (>)
        <оператор_сравнения> ::== <= (<=)
        <оператор_сравнения> ::== >= (>=)
        <оператор_сравнения> ::== == (==)
        <оператор_сравнения> ::== != (!=)
     */
    private void comparisonOperator() throws ParseException {
        Token token = current();

        if (token == null) {
            throw error("оператор сравнения");
        }

        switch (token.getType()) {
            case LESS:
                expect(TokenType.LESS);
                break;
            case GREATER:
                expect(TokenType.GREATER);
                break;
            case LESSOREQUAL:
                expect(TokenType.LESSOREQUAL);
                break;
            case GREATEROREQUAL:
                expect(TokenType.GREATEROREQUAL);
                break;
            case EQUAL:
                expect(TokenType.EQUAL);
                break;
            case NOTEQUAL:
                expect(TokenType.NOTEQUAL);
                break;
            default:
                throw error("оператор сравнения");
        }
    }

    private ParseException error(String expected) {
        Token token = current();

        if (token != null) {
            return new ParseException(
                    "Ожидалось " + expected +
                            ", найдено " + token.getType() +
                            " ('" + token.getValue() + "').",
                    token.getLine(), token.getColumn()
            );
        }

        int line = 1;
        int column = 1;

        if (!tokens.isEmpty()) {
            Token last = tokens.get(tokens.size() - 1);
            line = last.getLine();
            column = last.getColumn();
        }

        return new ParseException(
                "Ожидалось " + expected +
                        ", но обнаружен конец файла.", line, column
        );
    }

    //TODO: убрать заглушку
    private void skipExpressionTokens(int count, String expressionName) throws ParseException {
        for (int i = 0; i < count; i++) {
            if (current() == null) {
                throw error("продолжение " + expressionName);
            }
            advance();
        }
    }
}
