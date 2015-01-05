/**
 * Copyright (c) 2012-2015 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.runtime.objects;

import static com.github.anba.es6draft.runtime.AbstractOperations.*;
import static com.github.anba.es6draft.runtime.internal.Properties.createProperties;

import java.util.EnumMap;

import com.github.anba.es6draft.runtime.ExecutionContext;
import com.github.anba.es6draft.runtime.Realm;
import com.github.anba.es6draft.runtime.internal.Initializable;
import com.github.anba.es6draft.runtime.internal.ObjectAllocator;
import com.github.anba.es6draft.runtime.internal.Properties.Attributes;
import com.github.anba.es6draft.runtime.internal.Properties.Prototype;
import com.github.anba.es6draft.runtime.internal.Properties.Value;
import com.github.anba.es6draft.runtime.types.Constructor;
import com.github.anba.es6draft.runtime.types.Creatable;
import com.github.anba.es6draft.runtime.types.CreateAction;
import com.github.anba.es6draft.runtime.types.Intrinsics;
import com.github.anba.es6draft.runtime.types.PropertyDescriptor;
import com.github.anba.es6draft.runtime.types.ScriptObject;
import com.github.anba.es6draft.runtime.types.Type;
import com.github.anba.es6draft.runtime.types.builtins.BuiltinConstructor;

/**
 * <h1>19 Fundamental Objects</h1><br>
 * <h2>19.5 Error Objects</h2>
 * <ul>
 * <li>19.5.5 Native Error Types Used in This Standard
 * <li>19.5.6 NativeError Object Structure
 * <ul>
 * <li>19.5.6.1 NativeError Constructors
 * <li>19.5.6.2 Properties of the NativeError Constructors
 * </ul>
 * </ul>
 */
public final class NativeErrorConstructor extends BuiltinConstructor implements Initializable,
        Creatable<ErrorObject> {
    /**
     * 19.5.5 Native Error Types Used in This Standard
     * <ul>
     * <li>19.5.5.1 EvalError
     * <li>19.5.5.2 RangeError
     * <li>19.5.5.3 ReferenceError
     * <li>19.5.5.4 SyntaxError
     * <li>19.5.5.5 TypeError
     * <li>19.5.5.6 URIError
     * </ul>
     */
    public enum ErrorType {
        EvalError, RangeError, ReferenceError, SyntaxError, TypeError, URIError, InternalError;

        private Intrinsics prototype() {
            switch (this) {
            case EvalError:
                return Intrinsics.EvalErrorPrototype;
            case RangeError:
                return Intrinsics.RangeErrorPrototype;
            case ReferenceError:
                return Intrinsics.ReferenceErrorPrototype;
            case SyntaxError:
                return Intrinsics.SyntaxErrorPrototype;
            case TypeError:
                return Intrinsics.TypeErrorPrototype;
            case URIError:
                return Intrinsics.URIErrorPrototype;
            case InternalError:
                return Intrinsics.InternalErrorPrototype;
            default:
                throw new AssertionError();
            }
        }
    }

    private final ErrorType type;

    /**
     * Constructs a new NativeError constructor function.
     * 
     * @param realm
     *            the realm object
     * @param type
     *            the native error type
     */
    public NativeErrorConstructor(Realm realm, ErrorType type) {
        super(realm, type.name(), 1);
        this.type = type;
    }

    @Override
    public void initialize(ExecutionContext cx) {
        switch (type) {
        case EvalError:
            createProperties(cx, this, EvalErrorConstructorProperties.class);
            break;
        case RangeError:
            createProperties(cx, this, RangeErrorConstructorProperties.class);
            break;
        case ReferenceError:
            createProperties(cx, this, ReferenceErrorConstructorProperties.class);
            break;
        case SyntaxError:
            createProperties(cx, this, SyntaxErrorConstructorProperties.class);
            break;
        case TypeError:
            createProperties(cx, this, TypeErrorConstructorProperties.class);
            break;
        case URIError:
            createProperties(cx, this, URIErrorConstructorProperties.class);
            break;
        case InternalError:
            createProperties(cx, this, InternalErrorConstructorProperties.class);
            break;
        default:
            throw new AssertionError();
        }
    }

    @Override
    public NativeErrorConstructor clone() {
        return new NativeErrorConstructor(getRealm(), type);
    }

    /**
     * 19.5.6.1.1 NativeError (message)
     * <p>
     * <strong>Extension</strong>: NativeError (message, fileName, lineNumber, columnNumber)
     */
    @Override
    public ErrorObject call(ExecutionContext callerContext, Object thisValue, Object... args) {
        ExecutionContext calleeContext = calleeContext();
        Object message = argument(args, 0);

        /* step 1 (omitted) */
        /* steps 2-4 */
        ErrorObject obj;
        if (!(thisValue instanceof ErrorObject) || ((ErrorObject) thisValue).isInitialized()) {
            obj = OrdinaryCreateFromConstructor(calleeContext, this, type.prototype(),
                    NativeErrorObjectAllocator.INSTANCE);
        } else {
            obj = (ErrorObject) thisValue;
        }

        /* step 5 */
        obj.initialize();

        /* step 6 */
        if (!Type.isUndefined(message)) {
            CharSequence msg = ToString(calleeContext, message);
            PropertyDescriptor msgDesc = new PropertyDescriptor(msg, true, false, true);
            DefinePropertyOrThrow(calleeContext, obj, "message", msgDesc);
        }

        /* extension: fileName, lineNumber and columnNumber arguments */
        if (args.length > 1) {
            CharSequence fileName = ToString(calleeContext, args[1]);
            CreateDataProperty(calleeContext, obj, "fileName", fileName);
        }
        if (args.length > 2) {
            int lineNumber = ToInt32(calleeContext, args[2]);
            CreateDataProperty(calleeContext, obj, "lineNumber", lineNumber);
        }
        if (args.length > 3) {
            int columnNumber = ToInt32(calleeContext, args[3]);
            CreateDataProperty(calleeContext, obj, "columnNumber", columnNumber);
        }

        /* step 7 */
        return obj;
    }

    /**
     * 19.5.6.1.2 new NativeError (...argumentsList)
     */
    @Override
    public ScriptObject construct(ExecutionContext callerContext, Object... args) {
        return Construct(callerContext, this, args);
    }

    @Override
    public CreateAction<ErrorObject> createAction() {
        return NativeErrorCreate.INSTANCES.get(type);
    }

    /**
     * 19.5.6.2 Properties of the NativeError Constructors
     */
    public enum EvalErrorConstructorProperties {
        ;

        @Prototype
        public static final Intrinsics __proto__ = Intrinsics.Error;

        @Value(name = "length", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final int length = 1;

        @Value(name = "name", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final String name = "EvalError";

        /**
         * 19.5.6.2.1 NativeError.prototype
         */
        @Value(name = "prototype", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final Intrinsics prototype = Intrinsics.EvalErrorPrototype;
    }

    /**
     * 19.5.6.2 Properties of the NativeError Constructors
     */
    public enum RangeErrorConstructorProperties {
        ;

        @Prototype
        public static final Intrinsics __proto__ = Intrinsics.Error;

        @Value(name = "length", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final int length = 1;

        @Value(name = "name", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final String name = "RangeError";

        /**
         * 19.5.6.2.1 NativeError.prototype
         */
        @Value(name = "prototype", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final Intrinsics prototype = Intrinsics.RangeErrorPrototype;
    }

    /**
     * 19.5.6.2 Properties of the NativeError Constructors
     */
    public enum ReferenceErrorConstructorProperties {
        ;

        @Prototype
        public static final Intrinsics __proto__ = Intrinsics.Error;

        @Value(name = "length", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final int length = 1;

        @Value(name = "name", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final String name = "ReferenceError";

        /**
         * 19.5.6.2.1 NativeError.prototype
         */
        @Value(name = "prototype", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final Intrinsics prototype = Intrinsics.ReferenceErrorPrototype;
    }

    /**
     * 19.5.6.2 Properties of the NativeError Constructors
     */
    public enum SyntaxErrorConstructorProperties {
        ;

        @Prototype
        public static final Intrinsics __proto__ = Intrinsics.Error;

        @Value(name = "length", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final int length = 1;

        @Value(name = "name", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final String name = "SyntaxError";

        /**
         * 19.5.6.2.1 NativeError.prototype
         */
        @Value(name = "prototype", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final Intrinsics prototype = Intrinsics.SyntaxErrorPrototype;
    }

    /**
     * 19.5.6.2 Properties of the NativeError Constructors
     */
    public enum TypeErrorConstructorProperties {
        ;

        @Prototype
        public static final Intrinsics __proto__ = Intrinsics.Error;

        @Value(name = "length", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final int length = 1;

        @Value(name = "name", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final String name = "TypeError";

        /**
         * 19.5.6.2.1 NativeError.prototype
         */
        @Value(name = "prototype", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final Intrinsics prototype = Intrinsics.TypeErrorPrototype;
    }

    /**
     * 19.5.6.2 Properties of the NativeError Constructors
     */
    public enum URIErrorConstructorProperties {
        ;

        @Prototype
        public static final Intrinsics __proto__ = Intrinsics.Error;

        @Value(name = "length", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final int length = 1;

        @Value(name = "name", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final String name = "URIError";

        /**
         * 19.5.6.2.1 NativeError.prototype
         */
        @Value(name = "prototype", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final Intrinsics prototype = Intrinsics.URIErrorPrototype;
    }

    /**
     * 19.5.6.2 Properties of the NativeError Constructors
     */
    public enum InternalErrorConstructorProperties {
        ;

        @Prototype
        public static final Intrinsics __proto__ = Intrinsics.Error;

        @Value(name = "length", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final int length = 1;

        @Value(name = "name", attributes = @Attributes(writable = false, enumerable = false,
                configurable = true))
        public static final String name = "InternalError";

        /**
         * 19.5.6.2.1 NativeError.prototype
         */
        @Value(name = "prototype", attributes = @Attributes(writable = false, enumerable = false,
                configurable = false))
        public static final Intrinsics prototype = Intrinsics.InternalErrorPrototype;
    }

    private static final class NativeErrorObjectAllocator implements ObjectAllocator<ErrorObject> {
        static final ObjectAllocator<ErrorObject> INSTANCE = new NativeErrorObjectAllocator();

        @Override
        public ErrorObject newInstance(Realm realm) {
            return new ErrorObject(realm);
        }
    }

    private static final class NativeErrorCreate implements CreateAction<ErrorObject> {
        private static final EnumMap<ErrorType, NativeErrorCreate> INSTANCES;
        static {
            EnumMap<ErrorType, NativeErrorCreate> map = new EnumMap<>(ErrorType.class);
            for (ErrorType type : ErrorType.values()) {
                map.put(type, new NativeErrorCreate(type));
            }
            INSTANCES = map;
        }

        private final ErrorType type;

        private NativeErrorCreate(ErrorType type) {
            this.type = type;
        }

        @Override
        public ErrorObject create(ExecutionContext cx, Constructor constructor, Object... args) {
            return OrdinaryCreateFromConstructor(cx, constructor, type.prototype(),
                    NativeErrorObjectAllocator.INSTANCE);
        }
    }
}
