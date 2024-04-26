package jtagrgh.lox;

class BreakError extends RuntimeError {
    BreakError(Token token, String message) {
        super(token, message);
    }
}
