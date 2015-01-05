/**
 * Copyright (c) 2012-2015 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.ast.scope;

import java.util.List;
import java.util.Set;

import com.github.anba.es6draft.ast.FunctionDeclaration;
import com.github.anba.es6draft.ast.FunctionNode;

/**
 * Scope class for {@link FunctionNode} objects.
 */
public interface FunctionScope extends TopLevelScope {
    @Override
    FunctionNode getNode();

    /**
     * Returns the set of parameter names.
     * 
     * @return the parameter names
     */
    Set<Name> parameterNames();

    /**
     * Returns the implicit {@code arguments} binding or {@code null} if not applicable.
     * 
     * @return the implicit {@code arguments} binding or {@code null}
     */
    Name arguments();

    /**
     * Returns <code>true</code> for dynamically scoped objects, <code>false</code> otherwise.
     * <p>
     * A scope is considered dynamic if it can change during runtime, in other words that means it
     * contains a non-strict, direct-eval call.
     * 
     * @return <code>true</code> if a dynamic scope
     */
    boolean isDynamic();

    /**
     * Returns {@code true} if the <tt>super</tt> keyword is used within this function.
     * 
     * @return {@code true} if this function contains a <tt>super</tt> reference
     */
    boolean hasSuperReference();

    /**
     * Returns {@code true} if the <tt>arguments</tt> object needs to be allocated for this
     * function.
     * 
     * @return {@code true} if the <tt>arguments</tt> object needs to be allocated
     */
    boolean needsArguments();

    /**
     * Returns the function's web legacy block-level function declarations.
     * 
     * @return the function declarations
     */
    List<FunctionDeclaration> blockFunctions();
}
