package luca;

import java.util.List;
import java.util.Map;

class LucaClass implements LucaCallable {

    final String name;

    LucaClass(Sting name) {
	this.name = name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
	LucaInstance instance = new LucaInstance(this);
	return instance;
    }

    @Override
    public int arity() {
	return 0;
    }

    @Override
    public String toString() {
	return name;
    }
    
}
