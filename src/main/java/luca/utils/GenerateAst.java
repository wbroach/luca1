package luca.utils;

import java.io.IOException;                                        
import java.io.PrintWriter;
import java.io.File; 
import java.util.Arrays;                                           
import java.util.List;                                             

/** A class used to automate AST generation  */
public class GenerateAst {

    private static final String packageName = "luca"; 
    
    public static void main(String[] args) throws IOException {      
	if (args.length != 1) {                                        
	    System.err.println("Usage: generate_ast <output directory>");
	    System.exit(64);                                             
	}                                                              
	String outputDir = args[0];
	defineAst(outputDir, "Expr", Arrays.asList(
	   "Binary   : Expr left, Token operator, Expr right",
	   "Grouping : Expr expression",
	   "Literal  : Object value",
	   "Unary    : Token operator, Expr right"
        )); 
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
	String path = outPutDir + File.separator + baseName + ".java";
	PrintWriter writer = new PrintWriter(path, "UTF-8");

	writer.println("package " + packageName + ";");
	writer.println();
	writer.println("import java.util.List;");
	writer.println();
	writer.println("abstract class " + baseName + " {");

	for (String type : types) {
	    int sep = type.indexOf(':');
	    String className = type.substring(0, sep).trim(); 
	    String fields = type.substring(sep + 1, type.length()).trim();
	    defineType(writer, baseName, className, fields); 
	}
	
	writer.println("}"); // end of abstract class 
	writer.close(); 
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
	writer.println("  static class " + className + " extends " +  baseName + " {");
	writer.println("    " + className + "(" + fieldList + ") {");

	String[] fields = fieldList.split(", ");                     
	for (String field : fields) {                                
	    String name = field.split(" ")[1];                         
	    writer.println("      this." + name + " = " + name + ";"); 
	}

	writer.println("    }");
	writer.println();                                            
	for (String field : fields) {                                
	    writer.println("    final " + field + ";");                
	}                                                            
	
	writer.println("  }");
    }
    
} 
