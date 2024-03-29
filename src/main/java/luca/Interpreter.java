package luca;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr,Integer> locals = new HashMap<>();

    Interpreter() {
	globals.define("clock", new LucaCallable() {
		@Override
		public int arity() { return 0; }

		@Override
		public Object call(Interpreter interpreter, List<Object> arguments) {
		    return ((double) (System.currentTimeMillis() / 1000.0));
		}

		@Override
		public String toString() {
		    return "<native fn>";
		}
	    });
    }
    
    void interpret(List<Stmt> statements) {
	try {
	    for (Stmt statement : statements) {
		execute(statement);
	    }
	}
	catch (RuntimeError error) {
	    Luca.runtimeError(error);
	}
    }

    private void execute(Stmt stmt) {
	stmt.accept(this);
    }
    
    private Object evaluate(Expr expr) {
	return expr.accept(this);
    }

    void resolve(Expr expr, int depth) {
	locals.put(expr, depth);
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
	while (isTruthy(evaluate(stmt.condition))) {
	    execute(stmt.body);
	}

	return null;
    }
    
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
	executeBlock(stmt.statements, new Environment(environment));
	return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
	environment.define(stmt.name.lexeme, null);
	LucaClass _class = new LucaClass(stmt.name.lexeme);
	environment.assign(stmt.name, _class);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
	Environment previous = this.environment;
	try {
	    this.environment = environment;
	    for (Stmt statement : statements) {
		execute(statement);
	    }
	}
	finally {
	    this.environment = previous; // restore previous environment
	}
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
	Object value = null;
	if (stmt.initializer != null) {
	    value = evaluate(stmt.initializer);
	}

	environment.define(stmt.name.lexeme, value);
	return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
	LucaFunction function = new LucaFunction(stmt, environment);
	environment.define(stmt.name.lexeme, function);
	return null;
    }
    
    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
	Object value = evaluate(expr.value);

	if (locals.containsKey(expr)) {
	    environment.assignAt(locals.get(expr), expr.name, value);
	}
	else {
	    globals.assign(expr.name, value);
	}

	return value;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
	evaluate(stmt.expression);
	return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
	if (isTruthy(evaluate(stmt.condition))) {
	    execute(stmt.thenBranch);
	}
	else if (stmt.elseBranch != null) {
	    execute(stmt.elseBranch);
	}

	return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
	Object value = evaluate(stmt.expression);
	System.out.println(stringify(value));
	return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
	Object value = null;
	if (stmt.value != null) { value = evaluate(stmt.value); }

	throw new Return(value);
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
	Object left = evaluate(expr.left);

	if (expr.operator.type == TokenType.OR) {
	    if (isTruthy(left)) { return left; }
	}
	else {
	    if (!isTruthy(left)) { return left; }
	}

	return evaluate(expr.right);
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

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
	return lookUpVariable(expr.name, expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
	if (locals.containsKey(expr)) {
	    return environment.getAt(locals.get(expr), name.lexeme);
	}
	else {
	    return globals.get(name);
	}
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
				       "Operands must be two numbers or two strings."); 
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

    @Override
    public Object visitCallExpr(Expr.Call expr) {
	Object callee = evaluate(expr.callee);

	List<Object> arguments = new ArrayList<>();
	for (Expr argument : expr.arguments) {
	    arguments.add(evaluate(argument));
	}

	if (!(callee instanceof LucaCallable)) {
	    throw new RuntimeError(expr.paren, "Can only call functions and classes.");
	}

	LucaCallable function = (LucaCallable)callee;
	if (arguments.size() != function.arity()) {
	    throw new RuntimeError(expr.paren, "Expected " +
				   function.arity() + " arguments but got " +
				   arguments.size() + ".");
	}

	return function.call(this, arguments);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
	Object object = evaluate(expr.object);
	if (object instanceof LucaInstance) {
	    return ((LucaInstance) object).get(expr.name);
	}

	throw new RuntimeError(expr.name, "Only instances have properties.");
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
