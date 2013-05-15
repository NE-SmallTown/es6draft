/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.runtime.types;

import java.util.EnumMap;

/**
 * <h1>8 Types</h1><br>
 * <h2>8.1 ECMAScript Language Types</h2><br>
 * <h3>8.1.6 The Object Type</h3>
 * <ul>
 * <li>8.1.6.3 Well-Known Symbols and Intrinsics
 * </ul>
 */
public enum BuiltinSymbol {
    NONE, //

    /**
     * &#64;&#64;create
     */
    create,

    /**
     * &#64;&#64;hasInstance
     */
    hasInstance,

    /**
     * &#64;&#64;isRegExp
     */
    isRegExp,

    /**
     * &#64;&#64;iterator
     */
    iterator,

    /**
     * &#64;&#64;ToPrimitive
     */
    ToPrimitive,

    /**
     * &#64;&#64;toStringTag
     */
    toStringTag,

    /**
     * &#64;&#64;elementGet
     */
    elementGet,

    /**
     * &#64;&#64;elementSet
     */
    elementSet,

    ;

    /**
     * Returns a {@link Symbol} object for this {@link BuiltinSymbol}
     */
    public final Symbol get() {
        assert this != NONE;
        return symbols.get(this);
    }

    private static final EnumMap<BuiltinSymbol, Symbol> symbols;
    static {
        EnumMap<BuiltinSymbol, Symbol> map = new EnumMap<>(BuiltinSymbol.class);
        for (BuiltinSymbol builtin : values()) {
            if (builtin != NONE) {
                String name = "@@" + builtin.name();
                map.put(builtin, new Symbol(name, false));
            }
        }
        symbols = map;
    }
}
