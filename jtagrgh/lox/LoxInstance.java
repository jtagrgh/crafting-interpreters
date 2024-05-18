package jtagrgh.lox;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.ArrayList;

class LoxInstance {
    private LoxClass klass;
    protected final Map<String, Object> fields = new HashMap<>();

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    Object get(Interpreter interpreter, Token name) {

        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        ArrayList<LoxFunction> methodList = klass.getMethodStack(name.lexeme);
        Stack<LoxFunction> methodStack = new Stack<>();

        if (!methodList.isEmpty()) {
            for (LoxFunction method : methodList) {
                methodStack.push(method.bind(this));
            }
            LoxFunction top = methodStack.peek();
            methodStack.pop();
            interpreter.methodStack = methodStack;
            return top;
        }

        LoxFunction getter = klass.findGetter(name.lexeme);
        if (getter != null) {
            return getter.bind(this).call(interpreter, null);
        }

        throw new RuntimeError(name,
                               "Undefined property '" + name.lexeme + "'.");
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
