/**
 * Copyright (c) André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.ast;

import com.github.anba.es6draft.ast.scope.Name;

/**
 * <h1>12 ECMAScript Language: Expressions</h1>
 * <ul>
 * <li>12.1 Identifiers
 * </ul>
 */
public final class BindingIdentifier extends Binding {
    private final Name name;

    public BindingIdentifier(long beginPosition, long endPosition, String name) {
        super(beginPosition, endPosition);
        this.name = new Name(name);
    }

    /**
     * Returns the binding identifier's name.
     * 
     * @return the binding name
     */
    public Name getName() {
        return name;
    }

    @Override
    public <R, V> R accept(NodeVisitor<R, V> visitor, V value) {
        return visitor.visit(this, value);
    }

    @Override
    public <V> int accept(IntNodeVisitor<V> visitor, V value) {
        return visitor.visit(this, value);
    }

    @Override
    public <V> void accept(VoidNodeVisitor<V> visitor, V value) {
        visitor.visit(this, value);
    }
}
