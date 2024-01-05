/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * Set of OData filter functions for temporal types.
 */
public interface FilterExpressionTemporal
{
    /**
     * Returns a {@link ValueDate.Expression} that uses the given {@code operand} to filter for the {@code "date"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueDate.Expression}.
     */
    @Nonnull
    static ValueDate.Expression date( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("date", operand);
        return new ValueDate.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the
     * {@code "fractionalseconds"} portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression fractionalSeconds( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("fractionalseconds", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the
     * {@code "fractionalseconds"} portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueTimeOfDay} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression fractionalSeconds( @Nonnull final ValueTimeOfDay operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("fractionalseconds", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "second"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression second( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("second", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "second"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueTimeOfDay} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression second( @Nonnull final ValueTimeOfDay operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("second", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "minute"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression minute( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("minute", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "minute"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueTimeOfDay} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression minute( @Nonnull final ValueTimeOfDay operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("minute", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "hour"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression hour( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("hour", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "hour"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueTimeOfDay} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression hour( @Nonnull final ValueTimeOfDay operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("hour", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "day"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDate} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression day( @Nonnull final ValueDate operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("day", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "day"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression day( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("day", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "month"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDate} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression month( @Nonnull final ValueDate operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("month", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "month"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression month( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("month", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "year"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression year( @Nonnull final ValueDate operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("year", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the {@code "year"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression year( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("year", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueTimeOfDay.Expression} that uses the given {@code operand} to filter for the {@code "time"}
     * portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueTimeOfDay.Expression}.
     */
    @Nonnull
    static ValueTimeOfDay.Expression time( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("time", operand);
        return new ValueTimeOfDay.Expression(expression);
    }

    /**
     * Returns a {@link ValueDateTimeOffset.Expression} that uses the current date time ({@code "now")}.
     *
     * @return A {@link ValueDateTimeOffset.Expression}.
     */
    @Nonnull
    static ValueDateTimeOffset.Expression now()
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("now");
        return new ValueDateTimeOffset.Expression(expression);
    }

    /**
     * Returns a {@link ValueDateTimeOffset.Expression} that uses the maximum date time ({@code "maxdatetime")}.
     *
     * @return A {@link ValueDateTimeOffset.Expression}.
     */
    @Nonnull
    static ValueDateTimeOffset.Expression maxDateTime()
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("maxdatetime");
        return new ValueDateTimeOffset.Expression(expression);
    }

    /**
     * Returns a {@link ValueDateTimeOffset.Expression} that uses the minimum date time ({@code "mindatetime")}.
     *
     * @return A {@link ValueDateTimeOffset.Expression}.
     */
    @Nonnull
    static ValueDateTimeOffset.Expression minDateTime()
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("mindatetime");
        return new ValueDateTimeOffset.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the
     * {@code "totaloffsetseconds"} portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDuration} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression totalOffsetSeconds( @Nonnull final ValueDuration operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("totaloffsetseconds", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that uses the given {@code operand} to filter for the
     * {@code "totaloffsetminutes"} portion of a temporal value.
     *
     * @param operand
     *            The {@link ValueDateTimeOffset} to filter for.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression totalOffsetMinutes( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("totaloffsetminutes", operand);
        return new ValueNumeric.Expression(expression);
    }
}
