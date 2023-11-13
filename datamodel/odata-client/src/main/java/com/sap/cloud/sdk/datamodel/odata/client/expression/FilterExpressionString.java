/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * Set of OData filter functions for string types.
 */
public interface FilterExpressionString
{
    /**
     * Return a {@link ValueBoolean.Expression} that checks whether {@code operand1} matches the pattern
     * {@code operand2} ({@code "matchesPattern"}).
     *
     * @param operand1
     *            The potential subsequence.
     * @param operand2
     *            The pattern.
     * @return A {@link ValueBoolean.Expression}.
     */

    @Nonnull
    static
        ValueBoolean.Expression
        matchesPattern( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("matchesPattern", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueString.Expression} that converts the given {@code operand} to all lower case
     * ({@code "toLower"}).
     *
     * @param operand
     *            The operand to be converted to lower case.
     * @return A {@link ValueString.Expression}.
     */
    @Nonnull
    static ValueString.Expression toLower( @Nonnull final ValueString operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("tolower", operand);
        return new ValueString.Expression(expression);
    }

    /**
     * Returns a {@link ValueString.Expression} that converts the given {@code operand} to all upper case
     * ({@code "toUpper"}).
     *
     * @param operand
     *            The operand to be converted to upper case.
     * @return A {@link ValueString.Expression}.
     */
    @Nonnull
    static ValueString.Expression toUpper( @Nonnull final ValueString operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("toupper", operand);
        return new ValueString.Expression(expression);
    }

    /**
     * Returns a {@link ValueString.Expression} that removes leading and trailing whitespace from the given
     * {@code operand} ({@code "trim"}).
     *
     * @param operand
     *            The operand to be trimmed.
     * @return A {@link ValueString.Expression}.
     */
    @Nonnull
    static ValueString.Expression trim( @Nonnull final ValueString operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("trim", operand);
        return new ValueString.Expression(expression);
    }

    /**
     * Returns a {@link ValueString.Expression} that concatenates {@code operand1} and {@code operand2}
     * ({@code "concat"}).
     *
     * @param operand1
     *            The first operand.
     * @param operand2
     *            The second operand.
     * @return A {@link ValueString.Expression}.
     */
    @Nonnull
    static ValueString.Expression concat( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("concat", operand1, operand2);
        return new ValueString.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} contains {@code operand2}
     * ({@code "contains"}).
     *
     * @param operand1
     *            The potential super sequence.
     * @param operand2
     *            The potential subsequence.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression contains( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("contains", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} has {@code operand2} as a
     * substring ({@code "substringof"}).
     *
     * @param operand1
     *            The potential super sequence.
     * @param operand2
     *            The potential subsequence.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        substringOf( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("substringof", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} ends with {@code operand2}
     * ({@code "endswith"}).
     *
     * @param operand1
     *            The potential super sequence.
     * @param operand2
     *            The potential subsequence.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression endsWith( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("endswith", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} starts with {@code operand2}
     * ({@code "startswith"}).
     *
     * @param operand1
     *            The potential super sequence.
     * @param operand2
     *            The potential subsequence.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static
        ValueBoolean.Expression
        startsWith( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("startswith", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that returns the index of {@code operand2} in {@code operand1}
     * ({@code "indexof"}).
     *
     * @param operand1
     *            The potential super sequence.
     * @param operand2
     *            The potential subsequence.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression indexOf( @Nonnull final ValueString operand1, @Nonnull final ValueString operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("indexof", operand1, operand2);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that returns the length of {@code operand} ({@code "length"}).
     *
     * @param operand
     *            The operand.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression length( @Nonnull final ValueString operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("length", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueString.Expression} that returns a substring of {@code operand1} starting at
     * {@code operand2} ({@code "substring"}).
     *
     * @param operand1
     *            The operand.
     * @param operand2
     *            The start index.
     * @return A {@link ValueString.Expression}.
     */
    @Nonnull
    static ValueString.Expression substring( @Nonnull final ValueString operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("substring", operand1, operand2);
        return new ValueString.Expression(expression);
    }

    /**
     * Returns a {@link ValueString.Expression} that returns a substring of {@code operand1} starting at
     * {@code operand2} with length {@code operand3} ({@code "substring"}).
     *
     * @param operand1
     *            The operand.
     * @param operand2
     *            The start index.
     * @param operand3
     *            The length.
     * @return A {@link ValueString.Expression}.
     */
    @Nonnull
    static ValueString.Expression substring(
        @Nonnull final ValueString operand1,
        @Nonnull final ValueNumeric operand2,
        @Nonnull final ValueNumeric operand3 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("substring", operand1, operand2, operand3);
        return new ValueString.Expression(expression);
    }
}
