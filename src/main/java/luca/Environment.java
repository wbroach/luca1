package luca;

import java.util.Map;
import java.util.HashMap;

class Environment {

    final Environment enclosing;
    private final Map<String,Object> values = new HashMap<>();

    Environment(Environment enclosing) {
	this.environment = enclosing;
    }

    Environment() {
	this(null);
    }

    void define(String name, Object value) {
	values.put(name, value);
    }

    Object get(Token name) {
	if (values.containsKey(name.lexeme)) { return values.get(name.lexeme); }

	if (enclosing != null) { return enclosing.get(name); }

	throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'."); 
    }

    void assign(Token name, Object value) {
	if (values.containsKey(name.lexeme)) {
	    values.put(name.name, value);
	}
	else {
	    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}
    }
}
