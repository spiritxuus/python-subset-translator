package ru.study.pytranslator.parser.descending;

import ru.study.pytranslator.lexer.Token;
import ru.study.pytranslator.lexer.TokenType;

import java.util.List;

public class DescendingParser {
    private List<Token> tokens;
    private int index;

    public void descendingParser(List<Token> tokens){
        this.tokens = tokens;
        index = 0;
    }

    // TODO: скорректировать вывод
    public void getTokens(){
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public Token next(){
        return tokens.get(this.index);
    }

    //<прог > ::== P <спис_опер> (id if)
    public void program() throws Exception {
        operatorList();
    }

    //<спис_опер> ::== <опер> <X> (id if)
    public void operatorList() throws Exception {
        operator();
        operatorListTail();
    }

    /*
        <спис_опер> ::== <опер> <X>    (id if)
        <X> ::== ε                     #
        <X> ::== P <спис_опер>`        (id if)
        <спис_опер >` ::== <опер> <X>  (id if)

        <X> - OperatorListTail
     */
    public void operatorListTail() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.IDENTIFIER, TokenType.IF:
                this.index++;
                operatorList();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    /*
        <опер> ::== <множ> (id)
        <опер> ::== <if>   (if)
     */
    public void operator() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.IDENTIFIER:
                this.index++;
                assignment();
                break;
            case TokenType.IF:
                this.index++;
                ifStatement();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    //<if> ::== if <part> <список_elif> (if)
    public void ifStatement() throws Exception {
        Token tempLexem = next();

        if (tempLexem.getType() != TokenType.IF){
            throw new Exception("Error on index : " + this.index);
        }
        this.index++;
        part();
        elseIfList();
    }

    //<part> ::== <лог_выр> : P <спис_опер> (id lit ( not)
    public void part() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.IDENTIFIER, TokenType.LITERAL, TokenType.LPAR, TokenType.NOT:
                this.index++;
                logicalExpression();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    /*
        <список_elif> ::== elif <part> <список_elif>    (elif)
        <список_elif> ::== else : P <спис_опер>         (else)
        <список_elif> ::== ε                            #
     */
    public void elseIfList() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.ELIF:
                this.index++;
                part();
                break;
            case TokenType.ELSE:
                this.index++;
                operatorList();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    //<множ> ::== id = <Y> (id)
    public void assignment() throws Exception {
        Token tempLexem = next();

        if (tempLexem.getType() != TokenType.IDENTIFIER){
            throw new Exception("Error on index : " + this.index);
        }
        this.index++;
        assignmentTail();
    }

    /*
        <множ> ::== id = <Y>                (id)
        <Y> ::== <арифм_выр>                (id lit)
        <Y> ::== , <множ>` , <арифм_выр>    (id)
        <множ>` ::== id = <Y>               (id)

        <Y> - AssignmentTail
     */
    public void assignmentTail() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.IDENTIFIER:
                this.index++;
                arithmeticExpression();
                assignment();
                //??? CHECK AND CORRECT THIS
                break;
            case TokenType.LITERAL:
                this.index++;
                assignment();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    //<арифм_выр> ::== <elem> <Z> (id lit)
    public void arithmeticExpression() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.IDENTIFIER, TokenType.LITERAL:
                this.index++;
                element();
                arithmeticExpressionTail();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    /*
        <арифм_выр> ::== <elem> <Z>    (id lit)
        <Z> ::== ε                      #
        <Z> ::== <арифм_опер> , <elem> (+-/*)

        <Z> - ArithmeticExpressionTail
     */
    public void arithmeticExpressionTail() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.PLUS, TokenType.MINUS, TokenType.DIVISION, TokenType.MULTIPLICATION:
                this.index++;
                arithmeticOperator();
                element();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    /*
        <арифм_опер> ::== + (+)
        <арифм_опер> ::== - (-)
        <арифм_опер> ::== / (/)
        <арифм_опер> ::== * (*)
     */
    public void arithmeticOperator() throws Exception {
        Token tempLexem = next();
        if (tempLexem.getType() != TokenType.PLUS &&
                tempLexem.getType() != TokenType.MINUS &&
                tempLexem.getType() != TokenType.DIVISION &&
                tempLexem.getType() != TokenType.MULTIPLICATION){
            throw new Exception("Error on index : " + this.index);
        }
        next();
    }

    /*
        <elem> ::== id  (lit)
        <elem> ::== lit (id)
     */
    public void element() throws Exception {
        Token tempLexem = next();
        if (tempLexem.getType() != TokenType.IDENTIFIER &&
                tempLexem.getType() != TokenType.LITERAL){
            throw new Exception("Error on index : " + this.index);
        }
        next();
    }

    //<лог_выр> ::== <лог_и> <C> (id lit ( not)
    public void logicalExpression() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.IDENTIFIER, TokenType.LITERAL, TokenType.LPAR, TokenType.NOT:
                this.index++;
                logicalAnd();
                logicalExpressionTail();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    /*
       <лог_выр> ::== <лог_и> <C>  (id lit ( not)
       <C> ::== ε                  #
       <C> ::== || <лог_выр>`      (id lit ( not)
       <лог_выр>` ::== <лог_и> <C> (id lit ( not)

       <C> - LogicalExpressionTail
    */
    public void logicalExpressionTail() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.IDENTIFIER, TokenType.LITERAL, TokenType.LPAR, TokenType.NOT:
                this.index++;
                logicalExpression();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    //<лог_и> ::== <лог_опер> <V> (id lit ( not)
    public void logicalAnd() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.IDENTIFIER, TokenType.LITERAL, TokenType.LPAR, TokenType.NOT:
                this.index++;
                logicalOperator();
                logicalAndTail();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    /*
        <лог_и> ::== <лог_опер> <V>  (id lit ( not)
        <V> ::== ε                   #
        <V> ::== and <лог_и>`         (id lit ( not)
        <лог_и>` ::== <лог_опер> <V> (id lit ( not)

        <V> - LogicalAndTail
     */
    public void logicalAndTail() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.IDENTIFIER, TokenType.LITERAL, TokenType.LPAR, TokenType.NOT:
                this.index++;
                logicalAnd();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    /*
        <лог_опер> ::== <сравнение> (id lit)
        <лог_опер> ::== (<лог_выр>)     (()
        <лог_опер> ::== not <лог_опер> (not)
     */
    public void logicalOperator() throws Exception {
        Token tempLexem = next();
        switch (tempLexem.getType()){
            case TokenType.IDENTIFIER, TokenType.LITERAL:
                this.index++;
                comparison();
                break;
            case TokenType.LPAR:
                this.index++;
                logicalExpression();
                break;
            case TokenType.NOT:
                this.index++;
                logicalOperator();
                break;
            default:
                throw new Exception("Error on index : " + this.index);
        }
    }

    //<сравнение> ::== <арифм_выр> <оператор_сравнения> <арифм_выр> (id lit)
    public void comparison() throws Exception {
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
    public void comparisonOperator() throws Exception {
        Token tempLexem = next();
        if (tempLexem.getType() != TokenType.LESS &&
                tempLexem.getType() != TokenType.GREATER &&
                tempLexem.getType() != TokenType.LESSOREQUAL &&
                tempLexem.getType() != TokenType.GREATEROREQUAL &&
                tempLexem.getType() != TokenType.EQUAL &&
                tempLexem.getType() != TokenType.NOTEQUAL){
            throw new Exception("Error on index : " + this.index);
        }
        next();
    }
}
