/**
 * Copyright (c) 2012-2013 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.ast;

/**
 * <h1>11 Expressions</h1><br>
 * <h2>11.2 Left-Hand-Side Expressions</h2>
 * <ul>
 * <li>11.2.6 Tagged Templates
 * </ul>
 */
public class TemplateCallExpression extends Expression {
    private Expression base;
    private TemplateLiteral template;

    public TemplateCallExpression(Expression base, TemplateLiteral template) {
        this.base = base;
        this.template = template;
    }

    public Expression getBase() {
        return base;
    }

    public TemplateLiteral getTemplate() {
        return template;
    }

    @Override
    public <R, V> R accept(NodeVisitor<R, V> visitor, V value) {
        return visitor.visit(this, value);
    }
}
