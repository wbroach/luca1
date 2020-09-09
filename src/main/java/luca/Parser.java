package luca;

import java.util.List;

import static luca.TokenType.*;

class Parser {

    private final List<Token> tokens;
    private int current;

    Parser(List<Token> tokens) {
	this.tokens = tokens;
	this.current = 0;
    }

    private Expr expression() {
	return equality();
    }

    private Expr equality() {
	Expr rootExpr = comparision();

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
	    rootExpr = Expr.Binary(rootExpr, operator, rightExpr);
	}

	return rootExpr;
    }

    private Expr addition() {
	Expr rootExpr = multiplication();

	while(match(MINUS,PLUS)) {
	    Token operator = advance();
	    Expr right = multiplication();
	    rootExpr = Expr.Binary(rootExpr, operator, rightExpr);
	}

	return rootExpr;
    }

    private Expr multiplication() {
	Expr rootExpr = unary(); 

	while(match(MINUS,PLUS)) {
	    Token operator = advance();
	    Expr right = unary();
	    rootExpr = Expr.Binary(rootExpr, operator, rightExpr);
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
	Expr.Literal literal = null;
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

    private boolean check(TokenType type) {
	if (isAtEnd()) { return false; }
	return peek().type == type; 
    }

    private Token advance() {
	if (!isAtEnd()) {
	    return tokens.get(current++); 
	}
	else {
	    return EOF; 
	}
    }

    private boolean isAtEnd() {
	return peek().type == EOF;
    }

    private Token peek() {
	return tokens.get(current); 
    }

}
