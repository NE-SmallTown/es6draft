/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.runtime.objects;

import static com.github.anba.es6draft.runtime.AbstractOperations.ToString;
import static com.github.anba.es6draft.runtime.internal.Errors.throwReferenceError;
import static com.github.anba.es6draft.runtime.internal.Errors.throwSyntaxError;
import static com.github.anba.es6draft.runtime.internal.Properties.createProperties;
import static com.github.anba.es6draft.runtime.types.builtins.OrdinaryFunction.AddRestrictedFunctionProperties;

import com.github.anba.es6draft.Script;
import com.github.anba.es6draft.ScriptLoader;
import com.github.anba.es6draft.parser.Parser;
import com.github.anba.es6draft.parser.ParserException;
import com.github.anba.es6draft.parser.ParserException.ExceptionType;
import com.github.anba.es6draft.runtime.ExecutionContext;
import com.github.anba.es6draft.runtime.Realm;
import com.github.anba.es6draft.runtime.internal.Initialisable;
import com.github.anba.es6draft.runtime.internal.Properties.Attributes;
import com.github.anba.es6draft.runtime.internal.Properties.Prototype;
import com.github.anba.es6draft.runtime.internal.Properties.Value;
import com.github.anba.es6draft.runtime.types.Constructor;
import com.github.anba.es6draft.runtime.types.Intrinsics;
import com.github.anba.es6draft.runtime.types.builtins.BuiltinFunction;

/**
 * 
 */
public class GeneratorFunctionConstructor extends BuiltinFunction implements Constructor,
        Initialisable {
    public GeneratorFunctionConstructor(Realm realm) {
        super(realm);
    }

    @Override
    public void initialise(Realm realm) {
        createProperties(this, realm, Properties.class);
        AddRestrictedFunctionProperties(realm, this);
    }

    /**
     * GeneratorFunction (p1, p2, ... , pn, body)
     */
    @Override
    public Object call(Object thisValue, Object... args) {
        return construct(args);
    }

    /**
     * new GeneratorFunction (p1, p2, ... , pn, body)
     */
    @Override
    public Object construct(Object... args) {
        int argCount = args.length;
        StringBuilder p = new StringBuilder();
        CharSequence bodyText;
        if (argCount == 0) {
            bodyText = "";
        } else if (argCount == 1) {
            bodyText = ToString(realm(), args[0]);
        } else {
            Object firstArg = args[0];
            p.append(ToString(realm(), firstArg));
            int k = 2;
            for (; k < argCount; ++k) {
                Object nextArg = args[k - 1];
                CharSequence nextArgString = ToString(realm(), nextArg);
                p.append(',').append(nextArgString);
            }
            bodyText = ToString(realm(), args[k - 1]);
        }

        Script script = script(realm(), p, bodyText);
        ExecutionContext scriptCxt = ExecutionContext.newScriptExecutionContext(realm());
        return script.evaluate(scriptCxt);
    }

    private static Script script(Realm realm, CharSequence p, CharSequence bodyText) {
        try {
            Parser parser = new Parser("<GeneratorFunction>", 1);
            com.github.anba.es6draft.ast.Script parsedScript = parser.parseGenerator(p, bodyText);
            String className = realm.nextFunctionName();
            return ScriptLoader.load(className, parsedScript);
        } catch (ParserException e) {
            if (e.getExceptionType() == ExceptionType.ReferenceError) {
                throw throwReferenceError(realm, e.getMessageKey(), e.getMessageArguments());
            }
            throw throwSyntaxError(realm, e.getMessageKey(), e.getMessageArguments());
        }
    }

    /**
     * Properties of the GeneratorFunction Constructor
     */
    public enum Properties {
        ;

        @Prototype
        public static final Intrinsics __proto__ = Intrinsics.Function;

        /**
         * GeneratorFunction.prototype
         */
        @Value(name = "prototype", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final Intrinsics prototype = Intrinsics.Generator;

        /**
         * GeneratorFunction.length
         */
        @Value(name = "length", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final int length = 1;

        @Value(name = "name", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final String name = "GeneratorFunction";
    }
}