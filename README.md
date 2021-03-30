# LUCA1

## TODO
- Change Maven build to package into a jar with a well-formatted name, create shell script acting as executable (or, maybe, a C executable)
- Consolidate binary operators in Parser.java using lambdas (supplier)
- Add integer type
- Add ternary operator
- Add modulo, +=, -=, *=, /=, %=
- Add bitwise operators
- Change truthiness of lists to mirror python (i.e. true if size > 0)
- Add `val` i.e. const value
- Comparison and equality on strings (lexigraphically) - ch 7 challenge
- If either operand is string, convert other to string and then concat
- ...what about iterables? Add support?

## Overview
A tree-walk interpreter of a Javascript-like programming language

To run:
To compile/run the code as-is from the repository, you must have Maven installed. 


To recompile:

### Shoutouts
Special thanks to [emacsformacosx.com](https://emacsformacosx.com/) for hosting an Emacs distribution that allowed me
to write this interpreter (initially) on a 9-year-old busted Macbook. 


