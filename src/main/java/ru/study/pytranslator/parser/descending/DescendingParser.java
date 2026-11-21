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
    private void ifStatement() {

    }

    //<part> ::== <лог_выр> : P <спис_опер> (id lit ( not)
    private void part() {

    }

    /*
        <список_elif> ::== elif <part> <список_elif>    (elif)
        <список_elif> ::== else : P <спис_опер>         (else)
        <список_elif> ::== ε                            #
     */
    private void elseIfList() {

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
    private void arithmeticExpression() {

    }

    /*
        <арифм_выр> ::== <elem> <Z>    (id lit)
        <Z> ::== ε                      #
        <Z> ::== <арифм_опер> , <elem> (+-/*)

        <Z> - ArithmeticExpressionTail
     */
    private void arithmeticExpressionTail() {

    }

    /*
        <арифм_опер> ::== + (+)
        <арифм_опер> ::== - (-)
        <арифм_опер> ::== / (/)
        <арифм_опер> ::== * (*)
     */
    private void arithmeticOperator() {

    }

    /*
        <elem> ::== id  (lit)
        <elem> ::== lit (id)
     */
    private void element() {

    }

    //<лог_выр> ::== <лог_и> <C> (id lit ( not)
    private void logicalExpression() {

    }

    /*
       <лог_выр> ::== <лог_и> <C>  (id lit ( not)
       <C> ::== ε                  #
       <C> ::== || <лог_выр>`      (id lit ( not)
       <лог_выр>` ::== <лог_и> <C> (id lit ( not)

       <C> - LogicalExpressionTail
    */
    private void logicalExpressionTail() {

    }

    //<лог_и> ::== <лог_опер> <V> (id lit ( not)
    private void logicalAnd() {

    }

    /*
        <лог_и> ::== <лог_опер> <V>  (id lit ( not)
        <V> ::== ε                   #
        <V> ::== and <лог_и>`         (id lit ( not)
        <лог_и>` ::== <лог_опер> <V> (id lit ( not)

        <V> - LogicalAndTail
     */
    private void logicalAndTail() {

    }

    /*
        <лог_опер> ::== <сравнение> (id lit)
        <лог_опер> ::== (<лог_выр>)     (()
        <лог_опер> ::== not <лог_опер> (not)
     */
    private void logicalOperator() {

    }

    //<сравнение> ::== <арифм_выр> <оператор_сравнения> <арифм_выр> (id lit)
    private void comparison() {

    }

    /*
        <оператор_сравнения> ::== < (<)
        <оператор_сравнения> ::== > (>)
        <оператор_сравнения> ::== <= (<=)
        <оператор_сравнения> ::== >= (>=)
        <оператор_сравнения> ::== == (==)
        <оператор_сравнения> ::== != (!=)
     */
    private void comparisonOperator() {

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
}
