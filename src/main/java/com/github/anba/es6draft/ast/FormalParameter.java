/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.ast;

/**
 * <h1>14 ECMAScript Language: Functions and Classes</h1>
 * <ul>
 * <li>14.1 Function Definitions
 * </ul>
 */
public abstract class FormalParameter extends AstNode {
    protected FormalParameter(long sourcePosition) {
        super(sourcePosition);
    }
}
