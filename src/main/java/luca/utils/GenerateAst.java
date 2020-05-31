package luca.utils;

import java.io.IOException;                                        
import java.io.PrintWriter;                                        
import java.util.Arrays;                                           
import java.util.List;                                             

/** A class used to automate AST generation  */
public class GenerateAst {
    
    public static void main(String[] args) throws IOException {      
	if (args.length != 1) {                                        
	    System.err.println("Usage: generate_ast <output directory>");
	    System.exit(64);                                             
	}                                                              
	String outputDir = args[0];                                    
    }
    
} 
