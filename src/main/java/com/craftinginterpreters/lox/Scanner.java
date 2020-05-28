package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;   // tracks the beginning of the current lexeme
    private int current = 0; // represents the current unconsumed character
    private int line = 1; 
    
    Scanner(String source) {
	this.source = source; 
    }

    List<Token> scanTokens() {
	while(!isAtEnd()) {
	    start = current;
	    scanToken(); 
	}

	tokens.add(new Token(EOF, "", null, line));
	return tokens; 
    }

    private void scanToken() {
	char c = advance();
	switch (c) {
	    case '(': addToken(LEFT_PAREN); break;     
	    case ')': addToken(RIGHT_PAREN); break;    
	    case '{': addToken(LEFT_BRACE); break;     
	    case '}': addToken(RIGHT_BRACE); break;    
	    case ',': addToken(COMMA); break;          
	    case '.': addToken(DOT); break;            
	    case '-': addToken(MINUS); break;          
	    case '+': addToken(PLUS); break;           
	    case ';': addToken(SEMICOLON); break;      
	    case '*': addToken(STAR); break;
	    case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;      
	    case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;    
	    case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;      
	    case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
	    case '/':
		if (match('/')) {
		    // consume the comment, but do not add a token
		    // note that newline is not consumed, but will be in next call
		    while (peek() != '\n' && !isAtEnd()) {
			advance(); 
		    }
		}
		else {
		    addToken(SLASH); 
		}
		break;
	    case ' ':
	    case '\r':
	    case '\t':
		break;
	    case '\n':
		++line;
		break;
	    case '"': string(); break;
	    default:
		Lox.error(line, "Unexpected character.");
		break; 
	}
    }

    private void string() {
	while (peek() != '"' & !isAtEnd()) {
	    if (peek() == '\n') { ++line; }
	    advance(); 
	}

	if (isAtEnd()) {
	    Lox.error(line, "Unterminated string.");
	    return; 
	}

	advance(); // advance past closing '"' char
	addToken(STRING, source.substring(start + 1, current - 1)); // trim quotes
    }

    private boolean match(char expected) {                 
	if (isAtEnd()) { return false; }
	if (source.charAt(current) != expected) { return false; } 
	
	current++;                                           
	return true;                                         
    }

    private char peek() {
	if (isAtEnd()) {
	    return '\0'; 
	}
	else {
	    return source.charAt(current); 
	}
    }

    private boolean isAtEnd() {
	return current >= source.length(); 
    }

    private char advance() {
	return source.charAt(current++); 
    }

    private void addToken(TokenType type) {                
	addToken(type, null);                                
    }                                                      

    private void addToken(TokenType type, Object literal) {
	String text = source.substring(start, current);      
	tokens.add(new Token(type, text, literal, line));    
    }

}
