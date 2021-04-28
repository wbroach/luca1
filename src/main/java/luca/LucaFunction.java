package luca;

import java.util.List;

class LucaFunction implements LucaCallable {
    private final Stmt.Function declaration;

    LucaFunction(Stmt.Function declaration) {
	this.declaration = declaration;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
	Environment environment = new Environment(interpreter.globals);
	return null;
    }


}
