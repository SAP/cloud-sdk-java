/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import java.io.Serializable;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.expression.Expressions;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionLogical;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by all value types.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 * @param <PrimitiveT>
 *            Type of the value the filterable field holds.
 */
public interface FilterableValue<EntityT, PrimitiveT extends Serializable>
    extends
    Expressions.Operand,
    EntityReference<EntityT>
{
    /**
     *
     * Filter by expression "eq null".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> equalToNull()
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.equalTo(this, Expressions.Operand.NULL);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "ne null".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> notEqualToNull()
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.notEqualTo(this, Expressions.Operand.NULL);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "eq".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> equalTo( @Nonnull final FilterableValue<?, ?> operand )
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.equalTo(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "eq".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default FilterableBoolean<EntityT> equalTo( @Nullable final PrimitiveT operand )
    {
        final Expressions.Operand value = Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionLogical.equalTo(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "ne".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> notEqualTo( @Nonnull final FilterableValue<?, ?> operand )
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.notEqualTo(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "ne".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default FilterableBoolean<EntityT> notEqualTo( @Nullable final PrimitiveT operand )
    {
        final Expressions.Operand value = Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionLogical.notEqualTo(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "lt".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> lessThan( @Nonnull final FilterableValue<?, ?> operand )
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.lessThan(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "lt".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default FilterableBoolean<EntityT> lessThan( @Nonnull final PrimitiveT operand )
    {
        final Expressions.Operand value = Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionLogical.lessThan(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "le".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> lessThanEqual( @Nonnull final FilterableValue<?, ?> operand )
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.lessThanEquals(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "le".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default FilterableBoolean<EntityT> lessThanEqual( @Nonnull final PrimitiveT operand )
    {
        final Expressions.Operand value = Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionLogical.lessThanEquals(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "gt".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> greaterThan( @Nonnull final FilterableValue<?, ?> operand )
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.greaterThan(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "gt".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default FilterableBoolean<EntityT> greaterThan( @Nonnull final PrimitiveT operand )
    {
        final Expressions.Operand value = Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionLogical.greaterThan(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "ge".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> greaterThanEqual( @Nonnull final FilterableValue<?, ?> operand )
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.greaterThanEquals(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "ge".
     *
     * @param operand
     *            The generic operand to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for the provided Java literal.
     */
    @Nonnull
    default FilterableBoolean<EntityT> greaterThanEqual( @Nonnull final PrimitiveT operand )
    {
        final Expressions.Operand value = Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionLogical.greaterThanEquals(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "in".
     *
     * @param operands
     *            The generic operands to compare with.
     * @return The FluentHelper filter.
     * @throws IllegalArgumentException
     *             When there is no mapping found for any of the provided Java literals.
     */
    @SuppressWarnings( "unchecked" )
    @Nonnull
    default FilterableBoolean<EntityT> in( @Nonnull final PrimitiveT... operands )
    {
        final Expressions.Operand[] value =
            Arrays.stream(operands).map(Expressions::createOperand).toArray(Expressions.Operand[]::new);
        final ValueBoolean.Expression expression = FilterExpressionLogical.in(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "in".
     *
     * @param operand
     *            The generic operands to compare with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> in( @Nonnull final FilterableCollection<?, PrimitiveT> operand )
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.in(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }
}
