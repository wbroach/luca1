package luca;

class Interpreter implements Expr.Visitor<Object> {

    private Object evaluate(Expr expr) {
	return expr.accept(this);
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
		return (double) left > (double) right;
	    case GREATER_EQUAL:
		return (double) left >= (double) right;
	    case LESS:
		return (double) left < (double) right;
	    case LESS_EQUAL:
		return (double) left <= (double) right;
	    case MINUS:
		return (double) left - (double) right;
	    case PLUS:
		if (left instanceof Double && right instanceof Double) {
		    return (double) left + (double) right; 
		}

		if (left instance of String && right instanceof String) {
		    return (String) left.concat(right); 
		}
	    case SLASH:
		return (double) left / (double) right;
	    case STAR:
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

}
