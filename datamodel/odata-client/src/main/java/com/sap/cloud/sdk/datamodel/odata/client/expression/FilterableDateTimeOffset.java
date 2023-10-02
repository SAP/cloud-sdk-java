/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.time.Duration;

import javax.annotation.Nonnull;

/**
 * Date-Time-Offset operations for generic OData filter expression operands.
 */
public interface FilterableDateTimeOffset extends Expressions.Operand
{
    /**
     *
     * Filter by expression "date".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDate.Expression date()
    {
        return FilterExpressionTemporal.date(this::getExpression);
    }

    /**
     *
     * Filter by expression "fractionalseconds".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression timeFractionalSeconds()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        return FilterExpressionTemporal.fractionalSeconds(thisDateTime);
    }

    /**
     *
     * Filter by expression "second".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression timeSecond()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        return FilterExpressionTemporal.second(thisDateTime);
    }

    /**
     *
     * Filter by expression "minute".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression timeMinute()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        return FilterExpressionTemporal.minute(thisDateTime);
    }

    /**
     *
     * Filter by expression "hour".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression timeHour()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        return FilterExpressionTemporal.hour(thisDateTime);
    }

    /**
     *
     * Filter by expression "day".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression dateDay()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        return FilterExpressionTemporal.day(thisDateTime);
    }

    /**
     *
     * Filter by expression "month".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression dateMonth()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        return FilterExpressionTemporal.month(thisDateTime);
    }

    /**
     *
     * Filter by expression "year".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression dateYear()
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        return FilterExpressionTemporal.year(thisDateTime);
    }

    /**
     *
     * Filter by expression "time".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueTimeOfDay.Expression time()
    {
        return FilterExpressionTemporal.time(this::getExpression);
    }

    /**
     *
     * Filter by expression "offsetminutes".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression offsetMinutes()
    {
        return FilterExpressionTemporal.totalOffsetMinutes(this::getExpression);
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to add to the date time.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDateTimeOffset.Expression add( @Nonnull final ValueDuration operand )
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final ValueDuration value = operand::getExpression;
        return FilterExpressionArithmetic.add(thisDateTime, value);
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to add to the date time.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDateTimeOffset.Expression add( @Nonnull final Duration operand )
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        return FilterExpressionArithmetic.add(thisDateTime, value);
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The duration to subtract from the date time.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDateTimeOffset.Expression subtract( @Nonnull final ValueDuration operand )
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final ValueDuration value = operand::getExpression;
        return FilterExpressionArithmetic.subtract(thisDateTime, value);
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to subtract from the date time.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDateTimeOffset.Expression subtract( @Nonnull final Duration operand )
    {
        final ValueDateTimeOffset thisDateTime = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        return FilterExpressionArithmetic.subtract(thisDateTime, value);
    }
}
