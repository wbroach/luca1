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
	    statements.add(declaration());
	}

	return statements;
    }

    private Stmt declaration() {
	try {
	    if (match(VAR)) {
		advance(); // consume and discard
		return varDeclaration();
	    }
	    else {
		return statement();
	    }
	}
	catch (ParseError error) {
	    synchronize();
	    return null;
	}
    }

    private Stmt varDeclaration() {
	Token name = consume(IDENTIFIER, "Expect variable name.");

	Expr initializer = null;
	if (match(EQUAL)) {
	    advance(); // consume and discard
	    initializer = expression();
	}

	consume(SEMICOLON, "Expect ';' after variable declaration.");
	return new Stmt.Var(name, initializer);
    }

    private Stmt statement() {
	if (match(IF)) {
	    advance();
	    return ifStatement();
	}
	else if (match(PRINT)) {
	    advance(); // discard print token & advance
	    return printStatement();
	}
	else if (match(LEFT_BRACE)) {
	    advance();
	    return new Stmt.Block(block());
	}
	else {
	    return expressionStatement();
	}
    }

    private Stmt ifStatement() {
	consume(LEFT_PAREN, "Expect '(' after 'if'.");
	Expr condition = expression();
	consume(RIGHT_PAREN, "Expect ')' after if condition.");

	Stmt thenBranch = statement();
	Stmt elseBranch = null;
	if (match(ELSE)) {
	    elseBranch = statement();
	}

	return new Stmt.If(condition, thenBranch, elseBranch);
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

    private List<Stmt> block() {
	List<Stmt> statements = new ArrayList<>();

	while (!check(RIGHT_BRACE) && !isAtEnd()) {
	    statements.add(declaration());
	}

	consume(RIGHT_BRACE, "Expect '}' after block.");
	return statements;
    }

    private Expr expression() {
	return assignment();
    }

    private Expr assignment() {
	Expr rootExpr = or();

	if (match(EQUAL)) {
	    Token equals = advance();
	    Expr value = assignment();

	    if (rootExpr instanceof Expr.Variable) {
		Token name = ((Expr.Variable)rootExpr).name;
		return new Expr.Assign(name, value);
	    }

	    error(equals, "Invalid assignment target.");
	}

	return rootExpr;
    }

    private Expr or() {
	Expr rootExpr = and();

	while (match(OR)) {
	    Token operator = advance();
	    Expr right =  and();
	    rootExpr = new Expr.Logical(rootExpr, operator, right);
	}

	return rootExpr;
    }

    private Expr and() {
	Expr rootExpr = equality();

	while (match(AND)) {
	    Token operator  = advance();
	    Expr right = equality();
	    rootExpr = new Expr.Logical(rootExpr, operator, right);
	}

	return rootExpr;
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
	else if (next.type == IDENTIFIER) {
	    literal = new Expr.Variable(next);
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
