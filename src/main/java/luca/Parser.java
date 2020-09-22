package luca;

import java.util.List;
import java.util.ArrayList;

import static luca.TokenType.*;

class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current;

    Parser(List<Token> tokens) {
	this.tokens = tokens;
	this.current = 0;
    }

    List<Stmt> parse() {
	List<Stmt> statements = new ArrayList<>();
	while (!isAtEnd()) {
	    statements.add(statement());
	}

	return statements;
    }

    private Stmt statement() {
	if (match(PRINT)) {
	    advance(); // advance past the print token
	    return printStatement();
	}
	else {
	    return expressionStatement();
	}
    }

    private Stmt printStatement() {
	Expr value = expression();
	consume(SEMICOLON, "Expect ';' after value.");
	return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
	Expr expr = expression();
	consume(SEMICOLON, "Expect ';' after expression.");
	return new Stmt.Expression(expr);
    }

    private Expr expression() {
	return equality();
    }

    private Expr equality() {
	Expr rootExpr = comparison();

	while(match(BANG_EQUAL, EQUAL_EQUAL)) {
	    Token operator = advance();
	    Expr rightExpr = comparison();
	    rootExpr = new Expr.Binary(rootExpr, operator, rightExpr);
	}

	return rootExpr;
    }

    private Expr comparison() {
	Expr rootExpr = addition();

	while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
	    Token operator = advance();
	    Expr rightExpr = addition();
	    rootExpr = new Expr.Binary(rootExpr, operator, rightExpr);
	}

	return rootExpr;
    }

    private Expr addition() {
	Expr rootExpr = multiplication();

	while(match(MINUS, PLUS)) {
	    Token operator = advance();
	    Expr rightExpr = multiplication();
	    rootExpr = new Expr.Binary(rootExpr, operator, rightExpr);
	}

	return rootExpr;
    }

    private Expr multiplication() {
	Expr rootExpr = unary(); 

	while(match(STAR, SLASH)) {
	    Token operator = advance();
	    Expr rightExpr = unary();
	    rootExpr = new Expr.Binary(rootExpr, operator, rightExpr);
	}

	return rootExpr;
    }

    private Expr unary() {
	if (match(BANG, MINUS)) {
	    Token operator = advance();
	    Expr right = unary();
	    return new Expr.Unary(operator, right);
	}
	else {
	    return primary();
	}
    }

    private Expr primary() {
	Expr literal = null;
	Token next = advance();
	if (next.type == FALSE) {
	    literal = new Expr.Literal(false);
	}
	else if (next.type == TRUE) {
	    literal = new Expr.Literal(true);
	}
	else if (next.type == NIL) {
	    literal = new Expr.Literal(null);
	}
	else if (next.type == NUMBER || next.type == STRING) {
	    literal = new Expr.Literal(next.literal);
	}
	else if (next.type == LEFT_PAREN) {
	    Expr expr = expression();
	    consume(RIGHT_PAREN, "Expect ')' after expression.");
	    literal = new Expr.Grouping(expr);
	}
	else {
	    throw error(peek(), "Expression expected.");
	}

	return literal;
    }

    private boolean match(TokenType... types) {
	for (TokenType type : types) {
	    if (check(type)) {
		return true;
	    }
	}

	return false;
    }

    private Token consume(TokenType tok, String message) {
	if (check(tok)) {
	    return advance();
	}
	else {
	    throw error(peek(), message);
	}
    }

    private boolean check(TokenType type) {
	if (isAtEnd()) { return false; }
	return peek().type == type;
    }

    private Token advance() {
	return !isAtEnd() ? tokens.get(current++) : tokens.get(current);
    }

    private boolean isAtEnd() {
	return peek().type == EOF;
    }

    private Token peek() {
	return tokens.get(current);
    }

    private ParseError error(Token token, String message) {
	Luca.error(token, message);
	return new ParseError();
    }

    private void synchronize() {
	Token tok = advance();

	while (!isAtEnd()) {
	    if (tok.type == SEMICOLON) { return; }

	    if (checkKeyword(peek().type)) { return; }

	    tok = advance();
	}
    }

    private boolean checkKeyword(TokenType type) {
	return type == CLASS || type == FUN || type == VAR ||
	    type == FOR || type == IF || type == WHILE ||
	    type == PRINT || type == RETURN;
    }

}
