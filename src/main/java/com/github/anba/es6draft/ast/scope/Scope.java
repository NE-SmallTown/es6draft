/**
 * Copyright (c) 2012-2015 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.ast.scope;

import com.github.anba.es6draft.ast.ScopedNode;

/**
 * Base interface for scope information.
 */
public interface Scope {
    /**
     * Returns the parent scope.
     * 
     * @return the parent scope
     */
    Scope getParent();

    /**
     * Returns the {@link ScopedNode} for this scope object.
     * 
     * @return the node
     */
    ScopedNode getNode();

    /**
     * Returns <code>true</code> if {@code name} is declared in this scope.
     * 
     * @param name
     *            the variable name
     * @return <code>true</code> if {@code name} is declared
     */
    boolean isDeclared(Name name);

    /**
     * Returns the declared name for {@code name}.
     * 
     * @param name
     *            the variable name
     * @return the declared name
     */
    Name getDeclaredName(Name name);
}
