/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import static com.sap.cloud.sdk.datamodel.odata.client.expression.Expressions.createOperand;

import javax.annotation.Nonnull;

/**
 * Logical operations for generic OData filter expression operands.
 */
public interface FilterableComparisonRelative extends Expressions.Operand
{
    /**
     * Filter by expression "lt".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean lessThan( @Nonnull final Expressions.Operand operand )
    {
        return FilterExpressionLogical.lessThan(this, operand);
    }

    /**
     * Filter by expression "lt".
     *
     * @param operand
     *            The generic object to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default ValueBoolean lessThan( @Nonnull final Object operand )
    {
        final Expressions.Operand value = createOperand(operand);
        return lessThan(value);
    }

    /**
     * Filter by expression "le".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean lessThanEqual( @Nonnull final Expressions.Operand operand )
    {
        return FilterExpressionLogical.lessThanEquals(this, operand);
    }

    /**
     * Filter by expression "le".
     *
     * @param operand
     *            The generic object to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default ValueBoolean lessThanEqual( @Nonnull final Object operand )
    {
        final Expressions.Operand value = createOperand(operand);
        return lessThanEqual(value);
    }

    /**
     * Filter by expression "gt".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean greaterThan( @Nonnull final Expressions.Operand operand )
    {
        return FilterExpressionLogical.greaterThan(this, operand);
    }

    /**
     * Filter by expression "gt".
     *
     * @param operand
     *            The generic object to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default ValueBoolean greaterThan( @Nonnull final Object operand )
    {
        final Expressions.Operand value = createOperand(operand);
        return greaterThan(value);
    }

    /**
     * Filter by expression "ge".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean greaterThanEqual( @Nonnull final Expressions.Operand operand )
    {
        return FilterExpressionLogical.greaterThanEquals(this, operand);
    }

    /**
     * Filter by expression "ge".
     *
     * @param operand
     *            The generic object to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default ValueBoolean greaterThanEqual( @Nonnull final Object operand )
    {
        final Expressions.Operand value = createOperand(operand);
        return greaterThanEqual(value);
    }
}
