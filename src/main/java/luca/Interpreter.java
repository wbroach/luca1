package luca;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    void interpret(Expr expression) {
	try {
	    for (Stmt statement : statements) {
		execute(statement);
	    }
	}
	catch (RuntimeError error) {
	    Luca.runtimeError(error); 
	}
    }
    
    private Object evaluate(Expr expr) {
	return expr.accept(this);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
	evaluate(stmt.expression);
	return null;
    }

    @Override
    public void visitPrintStmt(Stmt.Print stmt) {
	Object value = evaluate(stmt.expression);
	System.out.println(stringify(value));
	return null;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
	return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
	Object right = evaluate(expr.right);

	switch (expr.operator.type) {
	    case BANG:
		return !isTruthy(right);
	    case MINUS:
		checkNumberOperand(expr.operator, right); 
		return -((double) right);
	}

	return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
	if (operand instanceof Double) { return; }
	throw new RuntimeError(operator, "Operand must be a number."); 
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
	if (left instanceof Double && right instanceof Double) { return; }
	throw new RuntimeError(operator, "Operands must be numbers."); 
    }

    private boolean isTruthy(Object obj) {
	// false and null are false, all else is true
	if (obj == null) { return false; }
	else if (obj instanceof Boolean) { return (boolean) obj; }
	else { return true; }
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
	return evaluate(expr.expression);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
	Object left = evaluate(expr.left);
	Object right = evaluate(expr.right);

	switch (expr.operator.type) {
	    case GREATER:
		checkNumberOperands(expr.operator, left, right);
		return (double) left > (double) right;
	    case GREATER_EQUAL:
		checkNumberOperands(expr.operator, left, right);
		return (double) left >= (double) right;
	    case LESS:
		checkNumberOperands(expr.operator, left, right);
		return (double) left < (double) right;
	    case LESS_EQUAL:
		checkNumberOperands(expr.operator, left, right);
		return (double) left <= (double) right;
	    case MINUS:
		checkNumberOperands(expr.operator, left, right);
		return (double) left - (double) right;
	    case PLUS:
		// these are the kind of checks to do if you want add
		// an integer type...check all applicable and throw
		// exception if none of them match
		if (left instanceof Double && right instanceof Double) {
		    return (double) left + (double) right;
		}

		if (left instanceof String && right instanceof String) {
		    return (String) left + (String) right;
		}

		throw new RuntimeError(expr.operator,
				       "Operands must be two numbers or two strings"); 
	    case SLASH:
		checkNumberOperands(expr.operator, left, right);
		return (double) left / (double) right;
	    case STAR:
		checkNumberOperands(expr.operator, left, right);
		return (double) left * (double) right;
	    case BANG_EQUAL: return !isEqual(left, right);
	    case EQUAL_EQUAL: return isEqual(left, right); 
	}

	return null;
    }

    private boolean isEqual(Object a, Object b) {
	if (a == null && b == null) {
	    return true; 
	}
	else if (a == null) {
	    return false; 
	}
	else {
	    return a.equals(b); 
	}
    }

    private String stringify(Object object) {
	if (object == null) { return "nil"; }

	if (object instanceof Double) {
	    String text = object.toString();
	    if (text.endsWith(".0")) {
		text = text.substring(0, text.length() - 2); 
	    }

	    return text;
	}

	return object.toString(); 
    }

}
