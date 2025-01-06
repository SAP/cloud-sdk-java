/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * Numeric operations for generic OData filter expression operands.
 */
public interface FilterableNumeric extends Expressions.Operand
{
    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The number to add.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric add( @Nonnull final ValueNumeric operand )
    {
        return FilterExpressionArithmetic.add(this::getExpression, operand);
    }

    /**
     *
     * Filter by expression "add".
     *
     * @param operand
     *            The number to add.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric add( @Nonnull final Number operand )
    {
        final ValueNumeric value = ValueNumeric.literal(operand);
        return FilterExpressionArithmetic.add(this::getExpression, value);
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The number to subtract.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric subtract( @Nonnull final ValueNumeric operand )
    {
        return FilterExpressionArithmetic.subtract(this::getExpression, operand);
    }

    /**
     *
     * Filter by expression "sub".
     *
     * @param operand
     *            The number to subtract.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric subtract( @Nonnull final Number operand )
    {
        final ValueNumeric value = ValueNumeric.literal(operand);
        return FilterExpressionArithmetic.subtract(this::getExpression, value);
    }

    /**
     *
     * Filter by expression "mul".
     *
     * @param operand
     *            The number to multiply.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric multiply( @Nonnull final ValueNumeric operand )
    {
        return FilterExpressionArithmetic.multiply((ValueNumeric) this::getExpression, operand);
    }

    /**
     *
     * Filter by expression "mul".
     *
     * @param operand
     *            The number to multiply.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric multiply( @Nonnull final Number operand )
    {
        final ValueNumeric value = ValueNumeric.literal(operand);
        return FilterExpressionArithmetic.multiply((ValueNumeric) this::getExpression, value);
    }

    /**
     *
     * Filter by expression "divby".
     *
     * @param operand
     *            The number to divide.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric divide( @Nonnull final ValueNumeric operand )
    {
        return FilterExpressionArithmetic.divide((ValueNumeric) this::getExpression, operand);
    }

    /**
     *
     * Filter by expression "divby".
     *
     * @param operand
     *            The number to divide.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric divide( @Nonnull final Number operand )
    {
        final ValueNumeric value = ValueNumeric.literal(operand);
        return FilterExpressionArithmetic.divide((ValueNumeric) this::getExpression, value);
    }

    /**
     *
     * Filter by expression "mod".
     *
     * @param operand
     *            The base number to calculate the modulo from.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric modulo( @Nonnull final ValueNumeric operand )
    {
        return FilterExpressionArithmetic.modulo(this::getExpression, operand);
    }

    /**
     *
     * Filter by expression "mod".
     *
     * @param operand
     *            The base number to calculate the modulo from.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric modulo( @Nonnull final Number operand )
    {
        final ValueNumeric value = ValueNumeric.literal(operand);
        return modulo(value);
    }

    /**
     *
     * Filter by expression "ceiling".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression ceil()
    {
        final ValueNumeric thisNumber = this::getExpression;
        return FilterExpressionArithmetic.ceiling(thisNumber);
    }

    /**
     *
     * Filter by expression "floor".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression floor()
    {
        final ValueNumeric thisNumber = this::getExpression;
        return FilterExpressionArithmetic.floor(thisNumber);
    }

    /**
     *
     * Filter by expression "round".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression round()
    {
        final ValueNumeric thisNumber = this::getExpression;
        return FilterExpressionArithmetic.round(thisNumber);
    }

    /**
     *
     * Filter by expression "-".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression negate()
    {
        final ValueNumeric thisNumber = this::getExpression;
        return FilterExpressionArithmetic.negate(thisNumber);
    }
}
