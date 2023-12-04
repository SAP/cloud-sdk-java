/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * Set of OData filter functions for logical types.
 */
public interface FilterExpressionLogical
{
    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} and {@code operand2} are equal
     * ({@code "eq"}).
     *
     * @param operand1
     *            The first operand.
     * @param operand2
     *            The second operand.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        equalTo( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("eq", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} and {@code operand2} are not equal
     * ({@code "ne"}).
     *
     * @param operand1
     *            The first operand.
     * @param operand2
     *            The second operand.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        notEqualTo( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("ne", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} is greater than {@code operand2}
     * ({@code "gt"}).
     *
     * @param operand1
     *            The first operand.
     * @param operand2
     *            The second operand.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        greaterThan( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("gt", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} is greater than or equal to
     * {@code operand2} ({@code "ge"}).
     *
     * @param operand1
     *            The first operand.
     * @param operand2
     *            The second operand.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        greaterThanEquals( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("ge", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} is less than {@code operand2}
     * ({@code "lt"}).
     *
     * @param operand1
     *            The first operand.
     * @param operand2
     *            The second operand.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        lessThan( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("lt", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} is less than or equal to
     * {@code operand2} ({@code "le"}).
     *
     * @param operand1
     *            The first operand.
     * @param operand2
     *            The second operand.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        lessThanEquals( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("le", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether both {@code operand1} and {@code operand2} are true
     * ({@code "and"}).
     *
     * @param operand1
     *            The first operand.
     * @param operand2
     *            The second operand.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression and( @Nonnull final ValueBoolean operand1, @Nonnull final ValueBoolean operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("and", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} or {@code operand2} is true
     * ({@code "or"}).
     *
     * @param operand1
     *            The first operand.
     * @param operand2
     *            The second operand.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression or( @Nonnull final ValueBoolean operand1, @Nonnull final ValueBoolean operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("or", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that negates the {@code operand} ({@code "not"}).
     *
     * @param operand
     *            The operand to negate.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression not( @Nonnull final ValueBoolean operand )
    {
        final FilterExpression expression = Expressions.createOperatorPrefix("not", operand);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} has {@code operand2}
     * ({@code "has"}).
     *
     * @param operand1
     *            The first operand.
     * @param operand2
     *            The second operand.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        has( @Nonnull final Expressions.OperandSingle operand1, @Nonnull final ValueEnum operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("has", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} is contained in {@code operands2}
     * ({@code "in"}).
     *
     * @param operand1
     *            The potential member.
     * @param operands2
     *            The potential container.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        in( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.Operand... operands2 )
    {
        final Expressions.OperandMultiple operand2 =
            ( protocol, prefixes ) -> "("
                + Arrays.stream(operands2).map(o -> o.getExpression(protocol)).collect(Collectors.joining(","))
                + ")";
        return in(operand1, operand2);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} is contained in {@code operand2}
     * ({@code "in"}).
     *
     * @param operand1
     *            The potential member.
     * @param operand2
     *            The potential container.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        in( @Nonnull final Expressions.Operand operand1, @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("in", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }
}
