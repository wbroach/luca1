# NOTE: This should be run from the main project directory!

javac src/main/java/luca/utils/GenerateAst.java
java -cp src/main/java luca.utils.GenerateAst src/main/java/luca
rm src/main/java/luca/utils/GenerateAst.class
