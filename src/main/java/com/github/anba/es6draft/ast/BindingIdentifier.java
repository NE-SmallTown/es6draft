/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.ast;

/**
 * <h1>12 Statements and Declarations</h1><br>
 * <h2>12.2 Declarations and the Variable Statement</h2>
 * <ul>
 * <li>12.2.1 Let and Const Declarations
 * </ul>
 */
public class BindingIdentifier extends Binding {
    private String name;

    public BindingIdentifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <R, V> R accept(NodeVisitor<R, V> visitor, V value) {
        return visitor.visit(this, value);
    }
}
