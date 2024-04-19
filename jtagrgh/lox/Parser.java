package jtagrgh.lox;

import java.util.List;
import static jtagrgh.lox.TokenType.*;

class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return comma();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr comma() {
        Expr expr = expression();

        while (match(COMMA)) {
            Token operator = previous();
            Expr right = expression();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr expression() {
        return ternary();
    }

    private Expr ternary() {
        Expr expr = equality();

        if (match(QMARK)) {
            Token leftOperator = previous();
            Expr middle = ternary();
            match(COLON);
            Token rightOperator = previous();
            Expr right = ternary();
            expr = new Expr.Ternary(expr,
                                    leftOperator,
                                    middle,
                                    rightOperator,
                                    right);
        }

        return expr;
    }

    private Expr equality() {
        if (check(BANG_EQUAL, EQUAL_EQUAL)) {
            return equalityNoLeft();
        }

        return equalityValid();
    }

    private Expr equalityNoLeft() {
        Lox.error(peek(), "Missing left operand.");

        Expr expr = null;

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr equalityValid() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        if (check(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            return comparisonNoLeft();
        }

        return comparisonValid();
    }

    private Expr comparisonNoLeft() {
        Lox.error(peek(), "Missing left operand.");

        Expr expr = null;

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparisonValid() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        if (check(MINUS, PLUS)) {
            return termNoLeft();
        }

        return termValid();
    }

    private Expr termNoLeft() {
        Lox.error(peek(), "Missing left operand");

        Expr expr = null;

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr termValid() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        if (check(SLASH, STAR)) {
            return factorNoLeft();
        }

        return factorValid();
    }

    private Expr factorNoLeft() {
        Lox.error(peek(), "Missing left operand.");

        Expr expr = null;

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factorValid() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }

    }

}
