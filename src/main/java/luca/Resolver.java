package luca;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;


class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;

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
}
