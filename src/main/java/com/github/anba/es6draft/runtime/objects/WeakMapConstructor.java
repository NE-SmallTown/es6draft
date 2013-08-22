/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.runtime.objects;

import static com.github.anba.es6draft.runtime.AbstractOperations.*;
import static com.github.anba.es6draft.runtime.internal.Errors.throwTypeError;
import static com.github.anba.es6draft.runtime.internal.Properties.createProperties;
import static com.github.anba.es6draft.runtime.objects.iteration.IterationAbstractOperations.GetIterator;
import static com.github.anba.es6draft.runtime.objects.iteration.IterationAbstractOperations.IteratorComplete;
import static com.github.anba.es6draft.runtime.objects.iteration.IterationAbstractOperations.IteratorNext;
import static com.github.anba.es6draft.runtime.objects.iteration.IterationAbstractOperations.IteratorValue;
import static com.github.anba.es6draft.runtime.types.Undefined.UNDEFINED;
import static com.github.anba.es6draft.runtime.types.builtins.OrdinaryFunction.AddRestrictedFunctionProperties;
import static com.github.anba.es6draft.runtime.types.builtins.OrdinaryFunction.OrdinaryConstruct;

import com.github.anba.es6draft.runtime.ExecutionContext;
import com.github.anba.es6draft.runtime.Realm;
import com.github.anba.es6draft.runtime.internal.Initialisable;
import com.github.anba.es6draft.runtime.internal.Messages;
import com.github.anba.es6draft.runtime.internal.ObjectAllocator;
import com.github.anba.es6draft.runtime.internal.Properties.Attributes;
import com.github.anba.es6draft.runtime.internal.Properties.Function;
import com.github.anba.es6draft.runtime.internal.Properties.Prototype;
import com.github.anba.es6draft.runtime.internal.Properties.Value;
import com.github.anba.es6draft.runtime.types.BuiltinSymbol;
import com.github.anba.es6draft.runtime.types.Callable;
import com.github.anba.es6draft.runtime.types.Intrinsics;
import com.github.anba.es6draft.runtime.types.ScriptObject;
import com.github.anba.es6draft.runtime.types.Type;
import com.github.anba.es6draft.runtime.types.builtins.BuiltinConstructor;

/**
 * <h1>15 Standard Built-in ECMAScript Objects</h1><br>
 * <h2>15.15 WeakMap Objects</h2>
 * <ul>
 * <li>15.15.1 The WeakMap Constructor
 * <li>15.15.2 Properties of the WeakMap Constructor
 * </ul>
 */
public class WeakMapConstructor extends BuiltinConstructor implements Initialisable {
    public WeakMapConstructor(Realm realm) {
        super(realm);
    }

    @Override
    public void initialise(ExecutionContext cx) {
        createProperties(this, cx, Properties.class);
        AddRestrictedFunctionProperties(cx, this);
    }

    /**
     * 15.15.1.1 WeakMap (iterable = undefined)
     */
    @Override
    public Object call(ExecutionContext callerContext, Object thisValue, Object... args) {
        ExecutionContext calleeContext = calleeContext();
        Object iterable = args.length > 0 ? args[0] : UNDEFINED;

        /* steps 1-4 */
        if (!Type.isObject(thisValue)) {
            // FIXME: spec bug ? `WeakMap()` no longer allowed (Bug 1406)
            throw throwTypeError(calleeContext, Messages.Key.NotObjectType);
        }
        if (!(thisValue instanceof WeakMapObject)) {
            throw throwTypeError(calleeContext, Messages.Key.IncompatibleObject);
        }
        WeakMapObject map = (WeakMapObject) thisValue;
        if (map.isInitialised()) {
            throw throwTypeError(calleeContext, Messages.Key.IncompatibleObject);
        }

        /* steps 5-7 */
        Object iter, adder = null;
        if (Type.isUndefinedOrNull(iterable)) {
            iter = UNDEFINED;
        } else {
            ScriptObject _iterable = ToObject(calleeContext, iterable);
            boolean hasValues = HasProperty(calleeContext, _iterable, "entries");
            if (hasValues) {
                iter = Invoke(calleeContext, _iterable, "entries");
            } else {
                iter = GetIterator(calleeContext, _iterable);
            }
            adder = Get(calleeContext, map, "set");
            if (!IsCallable(adder)) {
                throw throwTypeError(calleeContext, Messages.Key.NotCallable);
            }
        }

        /* step 8 */
        map.initialise();

        /* step 9 */
        if (Type.isUndefined(iter)) {
            return map;
        }
        /* step 10 */
        // explicit ToObject() call instead of implicit call through IteratorNext() -> Invoke()
        ScriptObject iterator = ToObject(calleeContext, iter);
        for (;;) {
            ScriptObject next = IteratorNext(calleeContext, iterator);
            boolean done = IteratorComplete(calleeContext, next);
            if (done) {
                return map;
            }
            Object nextValue = IteratorValue(calleeContext, next);
            ScriptObject entry = ToObject(calleeContext, nextValue);
            Object k = Get(calleeContext, entry, "0");
            Object v = Get(calleeContext, entry, "1");
            ((Callable) adder).call(calleeContext, map, k, v);
        }
    }

    /**
     * 15.15.1.2 new WeakMap (...argumentsList)
     */
    @Override
    public ScriptObject construct(ExecutionContext callerContext, Object... args) {
        return OrdinaryConstruct(callerContext, this, args);
    }

    /**
     * 15.15.2 Properties of the WeakMap Constructor
     */
    public enum Properties {
        ;

        @Prototype
        public static final Intrinsics __proto__ = Intrinsics.FunctionPrototype;

        @Value(name = "length", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final int length = 0;

        @Value(name = "name", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final String name = "WeakMap";

        /**
         * 15.15.2.1 WeakMap.prototype
         */
        @Value(name = "prototype", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final Intrinsics prototype = Intrinsics.WeakMapPrototype;

        /**
         * 15.15.2.2 WeakMap[ @@create ] ( )
         */
        @Function(name = "@@create", symbol = BuiltinSymbol.create, arity = 0,
                attributes = @Attributes(writable = false, enumerable = false, configurable = true))
        public static Object create(ExecutionContext cx, Object thisValue) {
            return OrdinaryCreateFromConstructor(cx, thisValue, Intrinsics.WeakMapPrototype,
                    WeakMapObjectAllocator.INSTANCE);
        }
    }

    private static class WeakMapObjectAllocator implements ObjectAllocator<WeakMapObject> {
        static final ObjectAllocator<WeakMapObject> INSTANCE = new WeakMapObjectAllocator();

        @Override
        public WeakMapObject newInstance(Realm realm) {
            return new WeakMapObject(realm);
        }
    }
}
