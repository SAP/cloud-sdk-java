/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.time.Duration;

import javax.annotation.Nonnull;

/**
 * Date operations for generic OData filter expression operands.
 */
public interface FilterableDate extends Expressions.Operand
{
    /**
     *
     * Filter by expression "day".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression dateDay()
    {
        final ValueDate thisDate = this::getExpression;
        return FilterExpressionTemporal.day(thisDate);
    }

    /**
     *
     * Filter by expression "month".
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default ValueNumeric.Expression dateMonth()
    {
        final ValueDate thisDate = this::getExpression;
        return FilterExpressionTemporal.month(thisDate);
    }

    /**
     *
     * Filter by expression "year".
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default ValueNumeric.Expression dateYear()
    {
        final ValueDate thisDate = this::getExpression;
        return FilterExpressionTemporal.year(thisDate);
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to add to the date expression.
     * @return The FluentHelper filter
     */
    @Nonnull
    default ValueDate.Expression add( @Nonnull final ValueDuration operand )
    {
        final ValueDate thisDate = this::getExpression;
        final ValueDuration value = operand::getExpression;
        return FilterExpressionArithmetic.add(thisDate, value);
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to ad to the date expression.
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default ValueDate.Expression add( @Nonnull final Duration operand )
    {
        final ValueDate thisDate = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        return FilterExpressionArithmetic.add(thisDate, value);
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The duration to subtract from the date.
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default ValueDate.Expression subtract( @Nonnull final ValueDuration operand )
    {
        final ValueDate thisDate = this::getExpression;
        final ValueDuration value = operand::getExpression;
        return FilterExpressionArithmetic.subtract(thisDate, value);
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The duration to subtract from the date.
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default ValueDate.Expression subtract( @Nonnull final Duration operand )
    {
        final ValueDate thisDate = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        return FilterExpressionArithmetic.subtract(thisDate, value);
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The other date to calculate the difference from.
     *
     * @return The FluentHelper filter
     */
    @Nonnull
    default ValueDuration.Expression difference( @Nonnull final ValueDate operand )
    {
        final ValueDate thisDate = this::getExpression;
        return FilterExpressionArithmetic.subtract(thisDate, operand);
    }
}
