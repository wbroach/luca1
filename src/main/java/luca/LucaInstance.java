package luca;

import java.util.Map;
import java.util.HashMap;

class LucaInstance {

    private LucaClass _class;
    private final Map<String,Object> fields = new HashMap<>();

    LucaInstance(LucaClass _class) {
	this._class = _class;
    }

    Object get(Token name) {
	if (fields.containsKey(name.lexeme)) {
	    return fields.get(name.lexeme);
	}

	throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    @Override
    public String toString() {
	return _class.name + " instance";
    }
    
}
