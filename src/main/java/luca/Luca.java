package luca; 

import java.io.BufferedReader;                               
import java.io.IOException;                                  
import java.io.InputStreamReader;                            
import java.nio.charset.Charset;                             
import java.nio.file.Files;                                  
import java.nio.file.Paths;                                  
import java.util.List;

public class Luca {

    static boolean hadError = false;

    public static void main(String[] args) throws IOException {
	if (args.length > 1) {                                   
	    System.out.println("Usage: luca [script]");            
	    System.exit(64); 
	}
	else if (args.length == 1) {                           
	    runFile(args[0]);                                      
	}
	else {                                                 
	    runPrompt();                                           
	}                                                        
    }

    private static void runFile(String path) throws IOException {
	byte[] bytes = Files.readAllBytes(Paths.get(path));        
	run(new String(bytes, Charset.defaultCharset()));          

	if (hadError) { System.exit(65); }
    }  

    private static void runPrompt() throws IOException {         
	InputStreamReader input = new InputStreamReader(System.in);
	BufferedReader reader = new BufferedReader(input);
	
	while (true) { 
	    System.out.print("> ");                                  
	    run(reader.readLine());
	    hadError = false; // if there is an error don't kill the entire session
	}                                                          
    }

    private static void run(String source) {    
	Scanner scanner = new Scanner(source);    
	List<Token> tokens = scanner.scanTokens();
	
	for (Token tok : tokens) {              
	    System.out.println(tok);              
	}                                         
    }

    static void error(int line, String message) {                       
	report(line, "", message);                                        
    }

    private static void report(int line, String where, String message) {
	System.err.println("[line " + line + "] Error" + where + ": " + message);        
	hadError = true;                                                  
    }  

}
