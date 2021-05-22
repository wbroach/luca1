package luca;

import java.util.List;

class LucaFunction implements LucaCallable {
    private final Stmt.Function declaration;

    LucaFunction(Stmt.Function declaration) {
	this.declaration = declaration;
    }

    @Override
    public int arity() {
	return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
	Environment environment = new Environment(interpreter.globals);
	for (int i = 0; i < declaration.params.size(); ++i) {
	    environment.define(declaration.params.get(i).lexeme, arguments.get(i));
	}

	try {
	    interpreter.executeBlock(declaration.body, environment);
	}
	catch (Return returnValue) {
	    return returnValue.value;
	}

	return null;
    }

    @Override
    public String toString() {
	return "<fn " + declaration.name.lexeme + ">";
    }

}
