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
	    Expr rightExp = comparison():
	    rootExpr = new Expr.Binary(rootExpr, operator, rightExpr); 
	}

	return rootExpr; 
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
