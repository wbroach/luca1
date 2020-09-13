package luca;

class Interpreter implements Expr.Visitor<Object> {

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
		return -((double) right);
	}

	return null;
    }

    private boolean isTruthy(Object obj) {
	if (obj == null) { return false; }
	else if (obj instanceof Boolean) { return (boolean) obj; }
	else { return true; }
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
	return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
	return expr.accept(this);
    }

}
