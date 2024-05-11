package jtagrgh.lox;

import java.util.List;
import java.util.Map;

class LoxClass extends LoxInstance implements LoxCallable {
    final String name;
    private final Map<String, LoxFunction> methods;
    private final Map<String, LoxFunction> statics;
    private final Map<String, LoxFunction> getters;

    LoxClass(String name, Map<String, LoxFunction> methods,
             Map<String, LoxFunction> statics,
             Map<String, LoxFunction> getters) {
        super(null);
        this.name = name;
        this.methods = methods;
        this.statics = statics;
        this.getters = getters;
    }

    public void initialize(Interpreter interpreter) {
        LoxFunction staticInitializer = statics.get("init");
        if (staticInitializer != null) {
            staticInitializer.bind(this).call(interpreter, null);
        }
    }

    LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        return null;
    }

    LoxFunction findGetter(String name) {
        if (getters.containsKey(name)) {
            return getters.get(name);
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call(Interpreter interpreter,
                       List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }

    @Override
    Object get(Interpreter interpreter, Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        LoxFunction staticMethod = statics.get(name.lexeme);

        if (staticMethod != null) return staticMethod.bind(this);

        throw new RuntimeError(name,
                               "Undefined static property '" + name.lexeme + "'.");
    }
    
}
