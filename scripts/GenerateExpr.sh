# NOTE: This should be run from the main project directory!
AST_PATH=src/main/java/luca


javac src/main/java/luca/utils/GenerateAst.java
java -cp src/main/java luca.utils.GenerateAst $AST_PATH
rm src/main/java/luca/utils/GenerateAst.class
