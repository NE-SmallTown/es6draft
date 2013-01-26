/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.ast;

/**
 * <h1>13 Functions and Generators</h1>
 * <ul>
 * <li>13.4 Generator Definitions
 * </ul>
 */
public interface GeneratorDefinition extends FunctionNode {
    BindingIdentifier getIdentifier();
}
