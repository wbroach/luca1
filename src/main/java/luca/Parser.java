package luca;

import java.util.List;

import static luca.TokenType.*;

class Parser {

    private final List<Token> tokens;
    private int current = 0; 
    
    Parser(List<Token> tokens) {
	this.token = tokens; 
    }
}
