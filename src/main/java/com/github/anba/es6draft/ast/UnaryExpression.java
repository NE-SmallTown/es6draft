/**
 * Copyright (c) 2012-2016 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.ast;

/**
 * <h1>12 ECMAScript Language: Expressions</h1><br>
 * <h2>12.4 Postfix Expressions</h2>
 * <ul>
 * <li>12.4.4 Postfix Increment Operator
 * <li>12.4.5 Postfix Decrement Operator
 * </ul>
 * <h2>12.5 Unary Operators</h2>
 * <ul>
 * <li>12.5.4 The delete Operator
 * <li>12.5.5 The void Operator
 * <li>12.5.6 The typeof Operator
 * <li>12.5.7 Prefix Increment Operator
 * <li>12.5.8 Prefix Decrement Operator
 * <li>12.5.9 Unary + Operator
 * <li>12.5.10 Unary - Operator
 * <li>12.5.11 Bitwise NOT Operator ( ~ )
 * <li>12.5.12 Logical NOT Operator ( ! )
 * </ul>
 */
public final class UnaryExpression extends Expression {
    public enum Operator {
        DELETE("delete"), VOID("void"), TYPEOF("typeof"), PRE_INC("++"), PRE_DEC("--"),
        POST_INC("++", true), POST_DEC("--", true), POS("+"), NEG("-"), BITNOT("~"), NOT("!");

        private final String name;
        private final boolean postfix;

        private Operator(String name) {
            this(name, false);
        }

        private Operator(String name, boolean postfix) {
            this.name = name;
            this.postfix = postfix;
        }

        /**
         * Returns the unary operator name.
         * 
         * @return the operator name
         */
        public String getName() {
            // TODO: This is not a 'name'.
            return name;
        }

        /**
         * Returns {@code true} if the operator is postfix.
         * 
         * @return {@code true} if postfix operator
         */
        public boolean isPostfix() {
            return postfix;
        }
    }

    private final Operator operator;
    private final Expression operand;
    private boolean completion = true;

    public UnaryExpression(long beginPosition, long endPosition, Operator operator,
            Expression operand) {
        super(beginPosition, endPosition);
        this.operator = operator;
        this.operand = operand;
    }

    /**
     * Returns the unary operator.
     * 
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * Returns the operand expression.
     * 
     * @return the operand
     */
    public Expression getOperand() {
        return operand;
    }

    @Override
    public Expression emptyCompletion() {
        completion = false;
        return this;
    }

    /**
     * Returns {@code true} if the completion value is used.
     * 
     * @return {@code true} if the completion value is used
     * @see #emptyCompletion()
     */
    public boolean hasCompletion() {
        return completion;
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
