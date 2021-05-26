package luca;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;


class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

    private final Interpreter interpreter;
    private final Stack<Map<String,Boolean>> scopes = new Stack<>();

    Resolver(Interpreter interpreter) {
	this.interpreter = interpreter;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
	beginScope();
	resolve(stmt.statements);
	endScope();
	return null;
    }

    @Override
    public Void vistVarStmt(Stmt.Var stmt) {
	declare(stmt.name);
	if (stmt.initializer != null) {
	    resolve(stmt.initializer);
	}
	define(stmt.name);
	return null;
    }

    void resolve(List<Statement> statements) {
	for (Stmt statement : statements) {
	    resolve(statement);
	}
    }

    private void resolve(Stmt stmt) {
	stmt.accept(this);
    }

    private void resolve(Expr expr) {
	expr.accept(this);
    }

    private void beginScope() {
	scopes.push(new HashMap<String,Boolean>());
    }

    private void endScope() {
	scopes.pop();
    }

    private void declare(Token name) {
	if (scopes.isEmpty()) { return; }
	scopes.peek().put(name.lexeme, false);
    }

    private void define(Token name) {
	if (scopes.isEmpty()) { return; }
	scopes.peek().put(name, true);
    }
    
}
