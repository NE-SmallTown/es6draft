/**
 * Copyright (c) 2012-2014 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.runtime.objects.iteration;

import static com.github.anba.es6draft.runtime.internal.Properties.createProperties;

import com.github.anba.es6draft.runtime.ExecutionContext;
import com.github.anba.es6draft.runtime.Realm;
import com.github.anba.es6draft.runtime.internal.Initializable;
import com.github.anba.es6draft.runtime.internal.ObjectAllocator;
import com.github.anba.es6draft.runtime.internal.Properties.Attributes;
import com.github.anba.es6draft.runtime.internal.Properties.Function;
import com.github.anba.es6draft.runtime.internal.Properties.Prototype;
import com.github.anba.es6draft.runtime.internal.Properties.Value;
import com.github.anba.es6draft.runtime.types.BuiltinSymbol;
import com.github.anba.es6draft.runtime.types.Intrinsics;
import com.github.anba.es6draft.runtime.types.builtins.OrdinaryObject;

/**
 * <h1>25 Control Abstraction Objects</h1><br>
 * <h2>25.2 GeneratorFunction Objects</h2>
 * <ul>
 * <li>25.2.3 Properties of the GeneratorFunction Prototype Object
 * </ul>
 */
public final class GeneratorFunctionPrototype extends OrdinaryObject implements Initializable {
    public GeneratorFunctionPrototype(Realm realm) {
        super(realm);
    }

    @Override
    public void initialize(ExecutionContext cx) {
        createProperties(cx, this, Properties.class);
    }

    /**
     * 25.2.3 Properties of the GeneratorFunction Prototype Object
     */
    public enum Properties {
        ;

        @Prototype
        public static final Intrinsics __proto__ = Intrinsics.FunctionPrototype;

        /**
         * 25.2.3.1 GeneratorFunction.prototype.constructor
         */
        @Value(name = "constructor", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final Intrinsics constructor = Intrinsics.GeneratorFunction;

        /**
         * 25.2.3.2 GeneratorFunction.prototype.prototype
         */
        @Value(name = "prototype", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final Intrinsics prototype = Intrinsics.GeneratorPrototype;

        /**
         * 25.2.3.3 GeneratorFunction.prototype [ @@toStringTag ]
         */
        @Value(name = "[Symbol.toStringTag]", symbol = BuiltinSymbol.toStringTag,
                attributes = @Attributes(writable = false, enumerable = false, configurable = true))
        public static final String toStringTag = "GeneratorFunction";

        /**
         * 25.2.3.4 GeneratorFunction.prototype [ @@create ] ( )
         * 
         * @param cx
         *            the execution context
         * @param thisValue
         *            the function this-value
         * @return the new uninitialized generator object
         */
        @Function(name = "[Symbol.create]", arity = 0, symbol = BuiltinSymbol.create,
                attributes = @Attributes(writable = true, enumerable = false, configurable = true))
        public static Object create(ExecutionContext cx, Object thisValue) {
            // FIXME: spec bug? change to writable=false?
            return OrdinaryCreateFromConstructor(cx, thisValue, Intrinsics.GeneratorPrototype,
                    GeneratorObjectAllocator.INSTANCE);
        }
    }

    private static final class GeneratorObjectAllocator implements ObjectAllocator<GeneratorObject> {
        static final ObjectAllocator<GeneratorObject> INSTANCE = new GeneratorObjectAllocator();

        @Override
        public GeneratorObject newInstance(Realm realm) {
            return new GeneratorObject(realm);
        }
    }
}
