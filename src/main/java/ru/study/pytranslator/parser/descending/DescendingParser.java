package ru.study.pytranslator.parser.descending;

import ru.study.pytranslator.lexer.Token;
import ru.study.pytranslator.lexer.TokenType;
import ru.study.pytranslator.parser.ParseException;

import java.util.List;

public class DescendingParser {
    private List<Token> tokens;
    private int index;

    public void descendingParser(List<Token> tokens){
        this.tokens = tokens;
        this.index = 0;
    }

    // TODO: скорректировать вывод
    public void getTokens(){
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public Token current(){
        if (index >= tokens.size()) {
            return null;
        }

        return tokens.get(index);
    }

    private void advance() { index++; }

    private Token expect(TokenType expected) throws ParseException {
        Token token = current();

        if (token == null || token.getType() != expected) {
            throw new Exception(
                    "На позиции " + index + ": ожидается " + expected +
                            ", найдено " + (token == null ? "EOF" : token.getType())
            );
        }

        advance();
        return token;
    }

    //<прог > ::== P <спис_опер> (id if)
    public void program() throws Exception {
        operatorList();
    }

    //<спис_опер> ::== <опер> <X> (id if)
    public void operatorList() throws Exception {

    }

    /*
        <спис_опер> ::== <опер> <X>    (id if)
        <X> ::== ε                     #
        <X> ::== P <спис_опер>`        (id if)
        <спис_опер >` ::== <опер> <X>  (id if)

        <X> - OperatorListTail
     */
    public void operatorListTail() throws Exception {

    }

    /*
        <опер> ::== <множ> (id)
        <опер> ::== <if>   (if)
     */
    public void operator() throws Exception {

    }

    //<if> ::== if <part> <список_elif> (if)
    public void ifStatement() throws Exception {

    }

    //<part> ::== <лог_выр> : P <спис_опер> (id lit ( not)
    public void part() throws Exception {

    }

    /*
        <список_elif> ::== elif <part> <список_elif>    (elif)
        <список_elif> ::== else : P <спис_опер>         (else)
        <список_elif> ::== ε                            #
     */
    public void elseIfList() throws Exception {

    }

    //<множ> ::== id = <Y> (id)
    public void assignment() throws Exception {

    }

    /*
        <множ> ::== id = <Y>                (id)
        <Y> ::== <арифм_выр>                (id lit)
        <Y> ::== , <множ>` , <арифм_выр>    (id)
        <множ>` ::== id = <Y>               (id)

        <Y> - AssignmentTail
     */
    public void assignmentTail() throws Exception {

    }

    //<арифм_выр> ::== <elem> <Z> (id lit)
    public void arithmeticExpression() throws Exception {

    }

    /*
        <арифм_выр> ::== <elem> <Z>    (id lit)
        <Z> ::== ε                      #
        <Z> ::== <арифм_опер> , <elem> (+-/*)

        <Z> - ArithmeticExpressionTail
     */
    public void arithmeticExpressionTail() throws Exception {

    }

    /*
        <арифм_опер> ::== + (+)
        <арифм_опер> ::== - (-)
        <арифм_опер> ::== / (/)
        <арифм_опер> ::== * (*)
     */
    public void arithmeticOperator() throws Exception {

    }

    /*
        <elem> ::== id  (lit)
        <elem> ::== lit (id)
     */
    public void element() throws Exception {

    }

    //<лог_выр> ::== <лог_и> <C> (id lit ( not)
    public void logicalExpression() throws Exception {

    }

    /*
       <лог_выр> ::== <лог_и> <C>  (id lit ( not)
       <C> ::== ε                  #
       <C> ::== || <лог_выр>`      (id lit ( not)
       <лог_выр>` ::== <лог_и> <C> (id lit ( not)

       <C> - LogicalExpressionTail
    */
    public void logicalExpressionTail() throws Exception {

    }

    //<лог_и> ::== <лог_опер> <V> (id lit ( not)
    public void logicalAnd() throws Exception {

    }

    /*
        <лог_и> ::== <лог_опер> <V>  (id lit ( not)
        <V> ::== ε                   #
        <V> ::== and <лог_и>`         (id lit ( not)
        <лог_и>` ::== <лог_опер> <V> (id lit ( not)

        <V> - LogicalAndTail
     */
    public void logicalAndTail() throws Exception {

    }

    /*
        <лог_опер> ::== <сравнение> (id lit)
        <лог_опер> ::== (<лог_выр>)     (()
        <лог_опер> ::== not <лог_опер> (not)
     */
    public void logicalOperator() throws Exception {

    }

    //<сравнение> ::== <арифм_выр> <оператор_сравнения> <арифм_выр> (id lit)
    public void comparison() throws Exception {

    }

    /*
        <оператор_сравнения> ::== < (<)
        <оператор_сравнения> ::== > (>)
        <оператор_сравнения> ::== <= (<=)
        <оператор_сравнения> ::== >= (>=)
        <оператор_сравнения> ::== == (==)
        <оператор_сравнения> ::== != (!=)
     */
    public void comparisonOperator() throws Exception {

    }
}
