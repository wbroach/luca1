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
	    Token operator = previous();
	    Expr rightExp = comparison():
	    rootExpr = new Expr.Binary(rootExpr, operator, rightExpr); 
	}

	return rootExpr; 
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

    private boolean check(TokenType type) {
	if (isAtEnd()) { return false; }
	return peek().type == type; 
    }

}
