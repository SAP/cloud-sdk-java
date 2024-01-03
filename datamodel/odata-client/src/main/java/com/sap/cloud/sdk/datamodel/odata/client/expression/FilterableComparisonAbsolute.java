/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import static com.sap.cloud.sdk.datamodel.odata.client.expression.Expressions.createOperand;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Logical operations for generic OData filter expression operands.
 */
public interface FilterableComparisonAbsolute extends Expressions.Operand
{
    /**
     * Filter by expression "eq".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean equalTo( @Nonnull final Expressions.Operand operand )
    {
        return FilterExpressionLogical.equalTo(this, operand);
    }

    /**
     * Filter by expression "eq".
     *
     * @param operand
     *            The generic object to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default ValueBoolean equalTo( @Nullable final Object operand )
    {
        final Expressions.Operand value = createOperand(operand);
        return equalTo(value);
    }

    /**
     * Filter by expression "in".
     *
     * @param operands
     *            The generic operands to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean in( @Nonnull final Expressions.Operand... operands )
    {
        return FilterExpressionLogical.in(this, operands);
    }

    /**
     * Filter by expression "in".
     *
     * @param operands
     *            The generic objects to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default ValueBoolean in( @Nonnull final Object... operands )
    {
        final Expressions.Operand[] value =
            Arrays.stream(operands).map(Expressions::createOperand).toArray(Expressions.Operand[]::new);
        return in(value);
    }

    /**
     * Filter by expression "in".
     *
     * @param collection
     *            A filterable collection reference.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean in( @Nonnull final FilterableCollection collection )
    {
        return FilterExpressionLogical.in(this, collection);
    }

    /**
     * Filter by expression "in".
     *
     * @param <T>
     *            Generic argument type for provided list items.
     * @param operands
     *            The generic objects to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default <T> ValueBoolean in( @Nonnull final List<T> operands )
    {
        final Expressions.Operand[] value =
            operands.stream().map(Expressions::createOperand).toArray(Expressions.Operand[]::new);
        return in(value);
    }

    /**
     * Filter by expression "ne".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean notEqualTo( @Nonnull final Expressions.Operand operand )
    {
        return FilterExpressionLogical.notEqualTo(this, operand);
    }

    /**
     * Filter by expression "ne".
     *
     * @param operand
     *            The generic object to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default ValueBoolean notEqualTo( @Nullable final Object operand )
    {
        final Expressions.Operand value = createOperand(operand);
        return notEqualTo(value);
    }

    /**
     * Filter by expression "eq null".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean equalToNull()
    {
        return equalTo(Expressions.Operand.NULL);
    }

    /**
     * Filter by expression "ne null".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean notEqualToNull()
    {
        return notEqualTo(Expressions.Operand.NULL);
    }
}
