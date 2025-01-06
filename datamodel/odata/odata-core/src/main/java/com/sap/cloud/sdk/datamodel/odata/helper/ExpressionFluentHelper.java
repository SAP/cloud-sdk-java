/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;

import lombok.extern.slf4j.Slf4j;

/**
 * Template class that represents query expressions. Instances of this object are used in query modifier methods of the
 * entity fluent helpers.
 *
 * Use either the expression methods from EntityField instances, or the logical operators
 * {@link #and(ExpressionFluentHelper) and} and {@link #or(ExpressionFluentHelper) or} as methods in this class.
 * Negation can be achieved by {@link #not() not}. Every logical operator creates and returns a new instance based on
 * the original expression object. Instantiating objects from this class directly can cause undefined results.
 *
 * @see ExpressionFluentHelper#not(ExpressionFluentHelper)
 *
 * @param <EntityT>
 *            VdmObject that the expression is operating on.
 */
@Slf4j
public class ExpressionFluentHelper<EntityT>
{
    private final ValueBoolean delegateExpression;

    /**
     * Creates a new helper based on an arbitrary, untyped filter expression.
     * <p>
     * Instances of this class can be used to pass an unchecked {@link ValueBoolean} to fluent helpers. This approach
     * discards type safety and is generally discouraged.
     *
     * @param delegateExpression
     *            The expression to delegate to.
     */
    public ExpressionFluentHelper( @Nonnull final ValueBoolean delegateExpression )
    {
        this.delegateExpression = delegateExpression;
    }

    /**
     * <p>
     * Boolean OR expression fluent helper.
     * </p>
     * <p>
     * <b>Please note:</b> <br>
     * Filter expressions chained together by logical operators are interpreted in the same order as their corresponding
     * methods are called. Since the Java language evaluates method calls from left to right, the fluent API design is
     * following the same principle. The implicit precedence is following the method invocation and not the underlying,
     * logical operators:
     *
     * <pre>
     *     A.or(B).and(C) <=> (A.or(B)).and(C)
     * </pre>
     *
     * </p>
     * <p>
     * <b>Recommendation:</b> <br>
     * Incorporate parentheses or introduce variables to reflect combined expressions:
     *
     * <pre>
     *     var AorB = A.or(B)
     *     AorB.and(C)
     * </pre>
     *
     * </p>
     *
     *
     * @param disjunctExpression
     *            Other expression to combine with.
     *
     * @return Fluent helper that represents a <i>((this) || other)</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> or( @Nonnull final ExpressionFluentHelper<EntityT> disjunctExpression )
    {
        return new ExpressionFluentHelper<>(delegateExpression.or(disjunctExpression.delegateExpression));
    }

    /**
     * <p>
     * Boolean AND expression fluent helper.
     * </p>
     * <p>
     * <b>Please note:</b> <br>
     * Filter expressions chained together by logical operators are interpreted in the same order as their corresponding
     * methods are called. Since the Java language evaluates method calls from left to right, the fluent API design is
     * following the same principle. The implicit precedence is following the method invocation and not the underlying,
     * logical operators:
     *
     * <pre>
     *     A.or(B).and(C) <=> (A.or(B)).and(C)
     * </pre>
     *
     * </p>
     * <p>
     * <b>Recommendation:</b> <br>
     * Incorporate parentheses or introduce variables to reflect combined expressions:
     *
     * <pre>
     *     var AorB = A.or(B)
     *     AorB.and(C)
     * </pre>
     *
     * </p>
     *
     * @param conjunctExpression
     *            Other expression to combine with.
     *
     * @return Fluent helper that represents a <i>((this) && other)</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> and( @Nonnull final ExpressionFluentHelper<EntityT> conjunctExpression )
    {
        return new ExpressionFluentHelper<>(delegateExpression.and(conjunctExpression.delegateExpression));
    }

    /**
     * Boolean NOT expression fluent helper.
     *
     * @return Fluent helper that represents a <i>not(this)</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> not()
    {
        return new ExpressionFluentHelper<>(delegateExpression.not());
    }

    /**
     * Boolean NOT expression fluent helper.
     *
     * @param <T>
     *            The type argument for the returned {@link ExpressionFluentHelper}
     * @param expression
     *            expression to be negated.
     *
     * @return Fluent helper that represents a <i>not(expression)</i> expression.
     */
    @Nonnull
    public static <T> ExpressionFluentHelper<T> not( @Nonnull final ExpressionFluentHelper<T> expression )
    {
        return expression.not();
    }

    @Nonnull
    ValueBoolean getDelegateExpressionWithoutOuterParentheses()
    {
        return ( prefixes, protocol ) -> {
            final String expression = delegateExpression.getExpression(prefixes, protocol);
            return expression.startsWith("(") && expression.endsWith(")")
                ? expression.substring(1, expression.length() - 1)
                : expression;
        };
    }
}
