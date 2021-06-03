package luca;

import java.util.Map;
import java.util.HashMap;

class LucaInstance {

    private LucaClass _class;

    LucaInstance(LucaClass _class) {
	this._class = _class;
    }

    @Override
    public String toString() {
	return _class.name + " instance";
    }
    
}
