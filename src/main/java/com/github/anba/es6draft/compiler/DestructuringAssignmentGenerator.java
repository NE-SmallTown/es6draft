/**
 * Copyright (c) 2012-2015 André Bargull
 * Alle Rechte vorbehalten / All Rights Reserved.  Use is subject to license terms.
 *
 * <https://github.com/anba/es6draft>
 */
package com.github.anba.es6draft.compiler;

import static com.github.anba.es6draft.compiler.DefaultCodeGenerator.ToPropertyKey;
import static com.github.anba.es6draft.semantics.StaticSemantics.PropName;

import java.util.Iterator;

import com.github.anba.es6draft.ast.*;
import com.github.anba.es6draft.compiler.DefaultCodeGenerator.ValType;
import com.github.anba.es6draft.compiler.assembler.Jump;
import com.github.anba.es6draft.compiler.assembler.MethodName;
import com.github.anba.es6draft.compiler.assembler.Type;
import com.github.anba.es6draft.compiler.assembler.Variable;

/**
 * <h1>12 ECMAScript Language: Expressions</h1><br>
 * <h2>12.14 Assignment Operators</h2>
 * <ul>
 * <li>12.14.5 Destructuring Assignment
 * </ul>
 */
final class DestructuringAssignmentGenerator {
    private static final class Methods {
        // class: AbstractOperations
        static final MethodName AbstractOperations_GetV = MethodName.findStatic(
                Types.AbstractOperations, "GetV",
                Type.methodType(Types.Object, Types.ExecutionContext, Types.Object, Types.Object));

        static final MethodName AbstractOperations_GetV_String = MethodName.findStatic(
                Types.AbstractOperations, "GetV",
                Type.methodType(Types.Object, Types.ExecutionContext, Types.Object, Types.String));

        // class: Reference
        static final MethodName Reference_putValue = MethodName.findVirtual(Types.Reference,
                "putValue", Type.methodType(Type.VOID_TYPE, Types.Object, Types.ExecutionContext));

        // class: ScriptRuntime
        static final MethodName ScriptRuntime_createRestArray = MethodName.findStatic(
                Types.ScriptRuntime, "createRestArray",
                Type.methodType(Types.ArrayObject, Types.Iterator, Types.ExecutionContext));

        static final MethodName ScriptRuntime_getIterator = MethodName.findStatic(
                Types.ScriptRuntime, "getIterator",
                Type.methodType(Types.Iterator, Types.Object, Types.ExecutionContext));

        static final MethodName ScriptRuntime_iteratorNextAndIgnore = MethodName.findStatic(
                Types.ScriptRuntime, "iteratorNextAndIgnore",
                Type.methodType(Type.VOID_TYPE, Types.Iterator));

        static final MethodName ScriptRuntime_iteratorNextOrUndefined = MethodName.findStatic(
                Types.ScriptRuntime, "iteratorNextOrUndefined",
                Type.methodType(Types.Object, Types.Iterator));

        // class: Type
        static final MethodName Type_isUndefined = MethodName.findStatic(Types._Type,
                "isUndefined", Type.methodType(Type.BOOLEAN_TYPE, Types.Object));
    }

    private DestructuringAssignmentGenerator() {
    }

    /**
     * stack: [value] {@literal ->} []
     * 
     * @param codegen
     *            the code generator
     * @param node
     *            the assignment pattern node
     * @param mv
     *            the expression visitor
     */
    static void DestructuringAssignment(CodeGenerator codegen, AssignmentPattern node,
            ExpressionVisitor mv) {
        DestructuringAssignmentEvaluation init = new DestructuringAssignmentEvaluation(codegen, mv);
        node.accept(init, null);
    }

    private static void PutValue(LeftHandSideExpression node, ValType type, ExpressionVisitor mv) {
        assert type == ValType.Reference : "lhs is not reference: " + type;
        mv.loadExecutionContext();
        mv.invoke(Methods.Reference_putValue);
    }

    private static abstract class RuntimeSemantics<V> extends DefaultVoidNodeVisitor<V> {
        protected final CodeGenerator codegen;
        protected final ExpressionVisitor mv;

        RuntimeSemantics(CodeGenerator codegen, ExpressionVisitor mv) {
            this.codegen = codegen;
            this.mv = mv;
        }

        protected final void DestructuringAssignmentEvaluation(AssignmentPattern node) {
            node.accept(new DestructuringAssignmentEvaluation(codegen, mv), null);
        }

        protected final void IteratorDestructuringAssignmentEvaluation(AssignmentElementItem node,
                Variable<Iterator<?>> iterator) {
            node.accept(new IteratorDestructuringAssignmentEvaluation(codegen, mv), iterator);
        }

        protected final void KeyedDestructuringAssignmentEvaluation(AssignmentProperty node,
                String key) {
            node.accept(new LiteralKeyedDestructuringAssignmentEvaluation(codegen, mv), key);
        }

        protected final void KeyedDestructuringAssignmentEvaluation(AssignmentProperty node,
                ComputedPropertyName key) {
            node.accept(new ComputedKeyedDestructuringAssignmentEvaluation(codegen, mv), key);
        }

        protected final ValType expression(Expression node, ExpressionVisitor mv) {
            return codegen.expression(node, mv);
        }

        protected final ValType expressionValue(Expression node, ExpressionVisitor mv) {
            return codegen.expressionValue(node, mv);
        }

        protected final ValType expressionBoxedValue(Expression node, ExpressionVisitor mv) {
            return codegen.expressionBoxedValue(node, mv);
        }

        @Override
        protected final void visit(Node node, V value) {
            throw new IllegalStateException();
        }
    }

    /**
     * 12.14.5.2 Runtime Semantics: DestructuringAssignmentEvaluation
     */
    private static final class DestructuringAssignmentEvaluation extends RuntimeSemantics<Void> {
        DestructuringAssignmentEvaluation(CodeGenerator codegen, ExpressionVisitor mv) {
            super(codegen, mv);
        }

        @Override
        public void visit(ArrayAssignmentPattern node, Void value) {
            // stack: [value] -> [iterator]
            Variable<Iterator<?>> iterator = mv.newScratchVariable(Iterator.class).uncheckedCast();
            mv.lineInfo(node);
            mv.loadExecutionContext();
            mv.invoke(Methods.ScriptRuntime_getIterator);
            mv.store(iterator);

            for (AssignmentElementItem element : node.getElements()) {
                IteratorDestructuringAssignmentEvaluation(element, iterator);
            }

            mv.freeVariable(iterator);
        }

        @Override
        public void visit(ObjectAssignmentPattern node, Void value) {
            for (AssignmentProperty property : node.getProperties()) {
                // stack: [value] -> [value, value]
                mv.dup();
                // stack: [value, value] -> [value]
                if (property.getPropertyName() == null) {
                    // AssignmentProperty : IdentifierReference Initializer{opt}
                    assert property.getTarget() instanceof IdentifierReference;
                    String name = ((IdentifierReference) property.getTarget()).getName();
                    KeyedDestructuringAssignmentEvaluation(property, name);
                } else {
                    // AssignmentProperty : PropertyName : AssignmentElement
                    String name = PropName(property.getPropertyName());
                    if (name != null) {
                        KeyedDestructuringAssignmentEvaluation(property, name);
                    } else {
                        PropertyName propertyName = property.getPropertyName();
                        assert propertyName instanceof ComputedPropertyName;
                        KeyedDestructuringAssignmentEvaluation(property,
                                (ComputedPropertyName) propertyName);
                    }
                }
            }
            // stack: [value] -> []
            mv.pop();
        }
    }

    /**
     * 12.14.5.3 Runtime Semantics: IteratorDestructuringAssignmentEvaluation
     */
    private static final class IteratorDestructuringAssignmentEvaluation extends
            RuntimeSemantics<Variable<Iterator<?>>> {
        IteratorDestructuringAssignmentEvaluation(CodeGenerator codegen, ExpressionVisitor mv) {
            super(codegen, mv);
        }

        @Override
        public void visit(Elision node, Variable<Iterator<?>> iterator) {
            // stack: [] -> []
            mv.load(iterator);
            mv.invoke(Methods.ScriptRuntime_iteratorNextAndIgnore);
        }

        @Override
        public void visit(AssignmentElement node, Variable<Iterator<?>> iterator) {
            LeftHandSideExpression target = node.getTarget();
            Expression initializer = node.getInitializer();

            /* step 1 */
            ValType refType = null;
            if (!(target instanceof AssignmentPattern)) {
                // stack: [] -> [lref]
                refType = expression(target, mv);
            }

            /* steps 2-5 */
            // stack: [(lref)] -> [(lref), v]
            mv.load(iterator);
            mv.invoke(Methods.ScriptRuntime_iteratorNextOrUndefined);

            /* step 6 */
            // stack: [(lref), v] -> [(lref), v']
            if (initializer != null) {
                Jump undef = new Jump();
                mv.dup();
                mv.invoke(Methods.Type_isUndefined);
                mv.ifeq(undef);
                {
                    mv.pop();
                    expressionBoxedValue(initializer, mv);
                }
                mv.mark(undef);
            }

            /* steps 7-8 */
            if (target instanceof AssignmentPattern) {
                // stack: [v'] -> []
                DestructuringAssignmentEvaluation((AssignmentPattern) target);
            } else {
                // stack: [lref, 'v] -> []
                PutValue(target, refType, mv);
            }
        }

        @Override
        public void visit(AssignmentRestElement node, Variable<Iterator<?>> iterator) {
            LeftHandSideExpression target = node.getTarget();

            /* steps 1-4 */
            // stack: [] -> [rest]
            mv.load(iterator);
            mv.loadExecutionContext();
            mv.invoke(Methods.ScriptRuntime_createRestArray);

            /* steps 5-7 */
            if (!(target instanceof AssignmentPattern)) {
                // FIXME: spec bug - evaluation order? (bug 3463)

                // stack: [rest] -> [lref, rest]
                ValType refType = expression(target, mv);
                mv.swap();

                // stack: [lref, rest] -> []
                PutValue(target, refType, mv);
            } else {
                // stack: [rest] -> []
                DestructuringAssignmentEvaluation((AssignmentPattern) target);
            }
        }
    }

    /**
     * 12.14.5.4 Runtime Semantics: KeyedDestructuringAssignmentEvaluation
     */
    private static abstract class KeyedDestructuringAssignmentEvaluation<PROPERTYNAME> extends
            RuntimeSemantics<PROPERTYNAME> {
        KeyedDestructuringAssignmentEvaluation(CodeGenerator codegen, ExpressionVisitor mv) {
            super(codegen, mv);
        }

        abstract ValType evaluatePropertyName(PROPERTYNAME propertyName);

        @Override
        public void visit(AssignmentProperty node, PROPERTYNAME propertyName) {
            LeftHandSideExpression target = node.getTarget();
            Expression initializer = node.getInitializer();

            // stack: [value] -> [cx, value]
            mv.loadExecutionContext();
            mv.swap();

            // stack: [cx, value] -> [cx, value, propertyName]
            ValType type = evaluatePropertyName(propertyName);

            /* steps 1-2 */
            // stack: [cx, value, propertyName] -> [v]
            if (type == ValType.String) {
                mv.invoke(Methods.AbstractOperations_GetV_String);
            } else {
                mv.invoke(Methods.AbstractOperations_GetV);
            }

            /* step 3 */
            // stack: [v] -> [v']
            if (initializer != null) {
                Jump undef = new Jump();
                mv.dup();
                mv.invoke(Methods.Type_isUndefined);
                mv.ifeq(undef);
                {
                    mv.pop();
                    expressionBoxedValue(initializer, mv);
                }
                mv.mark(undef);
            }

            /* steps 4-6 */
            if (target instanceof AssignmentPattern) {
                // stack: [v'] -> []
                DestructuringAssignmentEvaluation((AssignmentPattern) target);
            } else {
                // FIXME: spec bug - evaluation order? (bug 3463)

                // stack: [v'] -> [lref, 'v]
                ValType refType = expression(target, mv);
                mv.swap();

                // stack: [lref, 'v] -> []
                PutValue(target, refType, mv);
            }
        }
    }

    /**
     * 12.14.5.4 Runtime Semantics: KeyedDestructuringAssignmentEvaluation
     */
    private static final class LiteralKeyedDestructuringAssignmentEvaluation extends
            KeyedDestructuringAssignmentEvaluation<String> {
        LiteralKeyedDestructuringAssignmentEvaluation(CodeGenerator codegen, ExpressionVisitor mv) {
            super(codegen, mv);
        }

        @Override
        ValType evaluatePropertyName(String propertyName) {
            mv.aconst(propertyName);
            return ValType.String;
        }
    }

    /**
     * 12.14.5.4 Runtime Semantics: KeyedDestructuringAssignmentEvaluation
     */
    private static final class ComputedKeyedDestructuringAssignmentEvaluation extends
            KeyedDestructuringAssignmentEvaluation<ComputedPropertyName> {
        ComputedKeyedDestructuringAssignmentEvaluation(CodeGenerator codegen, ExpressionVisitor mv) {
            super(codegen, mv);
        }

        @Override
        ValType evaluatePropertyName(ComputedPropertyName propertyName) {
            // Runtime Semantics: Evaluation
            // ComputedPropertyName : [ AssignmentExpression ]
            ValType propType = expressionValue(propertyName.getExpression(), mv);
            return ToPropertyKey(propType, mv);
        }
    }
}
