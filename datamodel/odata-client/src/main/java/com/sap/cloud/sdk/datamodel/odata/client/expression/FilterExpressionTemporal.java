package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * Set of OData filter functions for temporal types.
 */
public interface FilterExpressionTemporal
{
    @Nonnull
    static ValueDate.Expression date( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("date", operand);
        return new ValueDate.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression fractionalSeconds( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("fractionalseconds", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression fractionalSeconds( @Nonnull final ValueTimeOfDay operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("fractionalseconds", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression second( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("second", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression second( @Nonnull final ValueTimeOfDay operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("second", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression minute( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("minute", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression minute( @Nonnull final ValueTimeOfDay operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("minute", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression hour( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("hour", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression hour( @Nonnull final ValueTimeOfDay operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("hour", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression day( @Nonnull final ValueDate operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("day", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression day( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("day", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression month( @Nonnull final ValueDate operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("month", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression month( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("month", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression year( @Nonnull final ValueDate operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("year", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression year( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("year", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueTimeOfDay.Expression time( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("time", operand);
        return new ValueTimeOfDay.Expression(expression);
    }

    @Nonnull
    static ValueDateTimeOffset.Expression now()
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("now");
        return new ValueDateTimeOffset.Expression(expression);
    }

    @Nonnull
    static ValueDateTimeOffset.Expression maxDateTime()
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("maxdatetime");
        return new ValueDateTimeOffset.Expression(expression);
    }

    @Nonnull
    static ValueDateTimeOffset.Expression minDateTime()
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("mindatetime");
        return new ValueDateTimeOffset.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression totalOffsetSeconds( @Nonnull final ValueDuration operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("totaloffsetseconds", operand);
        return new ValueNumeric.Expression(expression);
    }

    @Nonnull
    static ValueNumeric.Expression totalOffsetMinutes( @Nonnull final ValueDateTimeOffset operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("totaloffsetminutes", operand);
        return new ValueNumeric.Expression(expression);
    }
}
