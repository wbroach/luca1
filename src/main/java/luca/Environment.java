package luca;

import java.util.Map;
import java.util.HashMap;

class Environment {

    final Environment enclosing;
    private final Map<String,Object> values = new HashMap<>();

    Environment(Environment enclosing) {
	this.enclosing = enclosing;
    }

    Environment() {
	this(null); // global scope constructor, no parent
    }

    void define(String name, Object value) {
	values.put(name, value);
    }

    Object getAt(int distance, String name) {
	return ancestor(distance).values.get(name);
    }

    Environment ancestor(int distance) {
	Environment environment = this;
	for (int i = 0; i < distance; ++i) {
	    environment = environment.enclosing;
	}

	return environment;
    }
    
    Object get(Token name) {
	if (values.containsKey(name.lexeme)) { return values.get(name.lexeme); }

	if (enclosing != null) { return enclosing.get(name); }

	throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'."); 
    }

    void assign(Token name, Object value) {
	if (values.containsKey(name.lexeme)) {
	    values.put(name.lexeme, value);
	}
	else if (enclosing != null) {
	    enclosing.assign(name, value);
	}
	else {
	    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}
    }

    void assignAt(int distance, Token name, Object value) {
	ancestor(distance).values.put(name.lexeme, value);
    }
    
}
