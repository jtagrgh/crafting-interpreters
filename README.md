## jlox-interpreter

An implementation of an interpreter for the Lox language from [crafting-interpreters](https://www.craftinginterpreters.com). 


## Description

Lox is a dynamic, multi-paradigm, C-like language, that supports average language features
like variables, functions, classes, and more. This project follow Part 1 of the book, which
is designing a tree-walk interpreter in Java for the Lox language.


## Additional Language Features

In addition to the language features layed out in the book, as per the challenges, I have added:
- Ternary operator
- Comma operator
- Grammatical error production for missing left operands
- Addition between a string and a non-string type
- Divide by 0 errors
- Break statements
- Anonymous functions
- Syntax errors for unused variables
- Static class methods
- Static class fields / initializer
- Getter methods
