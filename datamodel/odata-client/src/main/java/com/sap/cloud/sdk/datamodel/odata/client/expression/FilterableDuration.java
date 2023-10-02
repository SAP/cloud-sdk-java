/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.time.Duration;

import javax.annotation.Nonnull;

/**
 * Duration operations for generic OData filter expression operands.
 */
public interface FilterableDuration extends Expressions.Operand
{
    /**
     *
     * Filter by expression "offsetseconds".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression offsetSeconds()
    {
        return FilterExpressionTemporal.totalOffsetSeconds(this::getExpression);
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to add to the duration.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDuration.Expression add( @Nonnull final ValueDuration operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueDuration value = operand::getExpression;
        return FilterExpressionArithmetic.add(thisDuration, value);
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The duration to add to the duration.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDuration.Expression add( @Nonnull final Duration operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        return FilterExpressionArithmetic.add(thisDuration, value);
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The duration to subtract from this duration.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDuration.Expression subtract( @Nonnull final ValueDuration operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueDuration value = operand::getExpression;
        return FilterExpressionArithmetic.subtract(thisDuration, value);
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The duration to subtract from this duration.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDuration.Expression subtract( @Nonnull final Duration operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        return FilterExpressionArithmetic.subtract(thisDuration, value);
    }

    /**
     *
     * Filter by expression "mul".
     *
     * @param operand
     *            The product to be used to multiply the duration.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDuration.Expression multiply( @Nonnull final ValueNumeric operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        return FilterExpressionArithmetic.multiply(thisDuration, value);
    }

    /**
     *
     * Filter by expression "mul".
     *
     * @param operand
     *            The product to be used to multiply the duration.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDuration.Expression multiply( @Nonnull final Number operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueNumeric value = ValueNumeric.literal(operand);
        return FilterExpressionArithmetic.multiply(thisDuration, value);
    }

    /**
     *
     * Filter by expression "div".
     *
     * @param operand
     *            The quotient to be used to divide the duration.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDuration.Expression divide( @Nonnull final ValueNumeric operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        return FilterExpressionArithmetic.divide(thisDuration, value);
    }

    /**
     *
     * Filter by expression "div".
     *
     * @param operand
     *            The quotient to be used to divide the duration.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDuration.Expression divide( @Nonnull final Number operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueNumeric value = ValueNumeric.literal(operand);
        return FilterExpressionArithmetic.divide(thisDuration, value);
    }

    /**
     *
     * Filter by expression "-".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueDuration.Expression negate()
    {
        final ValueDuration thisDuration = this::getExpression;
        return FilterExpressionArithmetic.negate(thisDuration);
    }
}
