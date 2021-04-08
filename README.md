# LUCA1

## TODO
- Change Maven build to package into a jar with a well-formatted name, create shell script acting as executable (or, maybe, a C executable)
- Consolidate binary operator code using lambdas
  - See `parser_refactor` branch for notes on this
- Add integer type
  - Probably going to need to create a `Number` superclass and then check at runtime which type it is?
  - This is probably a low-priority in terms of implementation
- Add ternary operator
- Add modulo, +=, -=, *=, /=, %=
- Add bitwise operators
- Change truthiness of lists to mirror python (i.e. true if size > 0)
- Change "fun" keyword to "def" or something different
- Add `val` i.e. const value
- Comparison and equality on strings (lexigraphically) - ch 7 challenge
- If either operand is string, convert other to string and then concat
- 
- ...what about iterables? Add support?

## Overview
A tree-walk interpreter of a Javascript-like programming language

To run:
To compile/run the code as-is from the repository, you must have Maven installed. 


To recompile:

## Shoutouts & Attribution
Special thanks to [emacsformacosx.com](https://emacsformacosx.com/) for hosting an Emacs distribution
that allowed me to write this interpreter (initially) on a 9-year-old busted Macbook.

luca1 is an implementation of Robert Nystrom's [Lox programming language](https://www.craftinginterpreters.com).




