/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * Set of OData filter functions for arithmetic types.
 */
public interface FilterExpressionArithmetic
{
    /**
     * Addition expression for numbers.
     *
     * @param operand1
     *            The first, numeric operand.
     * @param operand2
     *            The second, numeric operand.
     * @return A new numeric expression.
     */
    @Nonnull
    static ValueNumeric.Expression add( @Nonnull final ValueNumeric operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("add", operand1, operand2);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Addition expression for date time and duration.
     *
     * @param operand1
     *            The first, date-time-offset operand.
     * @param operand2
     *            The second, duration operand.
     * @return A new date-time-offset expression.
     */
    @Nonnull
    static
        ValueDateTimeOffset.Expression
        add( @Nonnull final ValueDateTimeOffset operand1, @Nonnull final ValueDuration operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("add", operand1, operand2);
        return new ValueDateTimeOffset.Expression(expression);
    }

    /**
     * Addition expression for duration and duration.
     *
     * @param operand1
     *            The first, duration operand.
     * @param operand2
     *            The second, duration operand.
     * @return A new duration expression.
     */
    @Nonnull
    static ValueDuration.Expression add( @Nonnull final ValueDuration operand1, @Nonnull final ValueDuration operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("add", operand1, operand2);
        return new ValueDuration.Expression(expression);
    }

    /**
     * Addition expression for date and duration.
     *
     * @param operand1
     *            The first, date operand.
     * @param operand2
     *            The second, duration operand.
     * @return A new date expression.
     */
    @Nonnull
    static ValueDate.Expression add( @Nonnull final ValueDate operand1, @Nonnull final ValueDuration operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("add", operand1, operand2);
        return new ValueDate.Expression(expression);
    }

    /**
     * Subtraction expression for numbers.
     *
     * @param operand1
     *            The first, numeric operand.
     * @param operand2
     *            The second, numeric operand.
     * @return A new numeric expression.
     */
    @Nonnull
    static
        ValueNumeric.Expression
        subtract( @Nonnull final ValueNumeric operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("sub", operand1, operand2);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Subtraction expression for date time and duration.
     *
     * @param operand1
     *            The first, date-time-offset operand.
     * @param operand2
     *            The second, duration operand.
     * @return A new date-time-offset expression.
     */
    @Nonnull
    static
        ValueDateTimeOffset.Expression
        subtract( @Nonnull final ValueDateTimeOffset operand1, @Nonnull final ValueDuration operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("sub", operand1, operand2);
        return new ValueDateTimeOffset.Expression(expression);
    }

    /**
     * Subtraction expression for duration and duration.
     *
     * @param operand1
     *            The first, duration operand.
     * @param operand2
     *            The second, duration operand.
     * @return A new duration expression.
     */
    @Nonnull
    static
        ValueDuration.Expression
        subtract( @Nonnull final ValueDuration operand1, @Nonnull final ValueDuration operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("sub", operand1, operand2);
        return new ValueDuration.Expression(expression);
    }

    /**
     * Subtraction expression for date and duration.
     *
     * @param operand1
     *            The first, date operand.
     * @param operand2
     *            The second, duration operand.
     * @return A new date expression.
     */
    @Nonnull
    static ValueDate.Expression subtract( @Nonnull final ValueDate operand1, @Nonnull final ValueDuration operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("sub", operand1, operand2);
        return new ValueDate.Expression(expression);
    }

    /**
     * Subtraction expression for date and date.
     *
     * @param operand1
     *            The first, date operand.
     * @param operand2
     *            The second, date operand.
     * @return A new duration expression.
     */
    @Nonnull
    static ValueDuration.Expression subtract( @Nonnull final ValueDate operand1, @Nonnull final ValueDate operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("sub", operand1, operand2);
        return new ValueDuration.Expression(expression);
    }

    /**
     * Negation expression for numbers.
     *
     * @param operand
     *            The first, numeric operand.
     * @return A new numeric expression.
     */
    @Nonnull
    static ValueNumeric.Expression negate( @Nonnull final ValueNumeric operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("-", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Negation expression for duration.
     *
     * @param operand
     *            The first, duration operand.
     * @return A new duration expression.
     */
    @Nonnull
    static ValueDuration.Expression negate( @Nonnull final ValueDuration operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("-", operand);
        return new ValueDuration.Expression(expression);
    }

    /**
     * Multiplication expression for numbers.
     *
     * @param operand1
     *            The first, numeric operand.
     * @param operand2
     *            The second, numeric operand.
     * @return A new numeric expression.
     */
    @Nonnull
    static
        ValueNumeric.Expression
        multiply( @Nonnull final ValueNumeric operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("mul", operand1, operand2);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Multiplication expression for duration and number.
     *
     * @param operand1
     *            The first, duration operand.
     * @param operand2
     *            The second, numeric operand.
     * @return A new duration expression.
     */
    @Nonnull
    static
        ValueDuration.Expression
        multiply( @Nonnull final ValueDuration operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("mul", operand1, operand2);
        return new ValueDuration.Expression(expression);
    }

    /**
     * Division expression for floating point numbers.
     *
     * @param operand1
     *            The first, numeric operand.
     * @param operand2
     *            The second, numeric operand.
     * @return A new numeric expression.
     */
    @Nonnull
    static ValueNumeric.Expression divide( @Nonnull final ValueNumeric operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("divby", operand1, operand2);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Division expression for integer numbers.
     *
     * @param operand1
     *            The first, numeric operand.
     * @param operand2
     *            The second, numeric operand.
     * @return A new numeric expression.
     */
    @Nonnull
    static
        ValueNumeric.Expression
        divideEuclidean( @Nonnull final ValueNumeric operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("div", operand1, operand2);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Division expression for duration.
     *
     * @param operand1
     *            The first, duration operand.
     * @param operand2
     *            The second, numeric operand.
     * @return A new duration expression.
     */
    @Nonnull
    static
        ValueDuration.Expression
        divide( @Nonnull final ValueDuration operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("div", operand1, operand2);
        return new ValueDuration.Expression(expression);
    }

    /**
     * Modulo expression for numbers.
     *
     * @param operand1
     *            The first, numeric operand.
     * @param operand2
     *            The second, numeric operand.
     * @return A new numeric expression.
     */
    @Nonnull
    static ValueNumeric.Expression modulo( @Nonnull final ValueNumeric operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionInfix("mod", operand1, operand2);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Ceil expression for numbers.
     *
     * @param operand
     *            The numeric operand.
     * @return A new numeric expression.
     */
    @Nonnull
    static ValueNumeric.Expression ceiling( @Nonnull final ValueNumeric operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("ceiling", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Floor expression for numbers.
     *
     * @param operand
     *            The numeric operand.
     * @return A new numeric expression.
     */
    @Nonnull
    static ValueNumeric.Expression floor( @Nonnull final ValueNumeric operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("floor", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Round expression for numbers.
     *
     * @param operand
     *            The numeric operand.
     * @return A new numeric expression.
     */
    @Nonnull
    static ValueNumeric.Expression round( @Nonnull final ValueNumeric operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("round", operand);
        return new ValueNumeric.Expression(expression);
    }
}
