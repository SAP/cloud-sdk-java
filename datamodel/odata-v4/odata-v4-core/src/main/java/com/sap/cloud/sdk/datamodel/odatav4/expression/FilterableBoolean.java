/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.Expressions;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionLogical;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by Boolean.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 */
public interface FilterableBoolean<EntityT> extends FilterableValue<EntityT, Boolean>
{
    /**
     * Wrapper expression class, which delegates to another operation.
     *
     * @param <EntityT>
     *            Type of the entity which references the value.
     */
    @RequiredArgsConstructor
    @Getter
    class Expression<EntityT> implements FilterableBoolean<EntityT>, FilterExpression
    {
        @Delegate
        private final FilterExpression delegate;

        @Nonnull
        private final Class<EntityT> entityType;
    }

    /***
     * Creates a new Expression from a custom filter expression. Allows for untyped expressions to be supplied to the
     * VDM.
     *
     * @param delegateExpression
     *            The expression to which the Expression delegates.
     * @param entityType
     *            The expected entity type.
     * @param <EntityT>
     *            Type of the entity which references the value.
     * @return A new Expression.
     *
     * @since 4.8.0
     */
    @Nonnull
    static <EntityT> FilterableBoolean<EntityT> fromCustomFilter(
        @Nonnull final ValueBoolean delegateExpression,
        @Nonnull final Class<EntityT> entityType )
    {
        return new Expression<>((FilterExpression) delegateExpression, entityType);
    }

    /**
     * Combine current filter expression with another expression in conjunction.
     *
     * @param operand
     *            The other expression.
     * @return This FluentHelper reference.
     */
    @Nonnull
    default FilterableBoolean<EntityT> and( @Nonnull final FilterableBoolean<EntityT> operand )
    {
        final ValueBoolean value = operand::getExpression;
        final ValueBoolean.Expression expression = FilterExpressionLogical.and(this::getExpression, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     * Combine the filter expression with a boolean value in conjunction.
     *
     * @param operand
     *            A boolean value.
     * @return This FluentHelper reference.
     */
    @Nonnull
    default FilterableBoolean<EntityT> and( @Nonnull final Boolean operand )
    {
        final ValueBoolean value = (ValueBoolean) Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionLogical.and(this::getExpression, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     * Combine the filter expression with another expression in disjunction.
     *
     * @param operand
     *            The other expression.
     * @return This FluentHelper reference.
     */
    @Nonnull
    default FilterableBoolean<EntityT> or( @Nonnull final FilterableBoolean<EntityT> operand )
    {
        final ValueBoolean value = operand::getExpression;
        final ValueBoolean.Expression expression = FilterExpressionLogical.or(this::getExpression, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     * Combine the filter expression with a boolean value in disjunction.
     *
     * @param operand
     *            The other expression.
     * @return This FluentHelper reference.
     */
    @Nonnull
    default FilterableBoolean<EntityT> or( @Nonnull final Boolean operand )
    {
        final ValueBoolean value = (ValueBoolean) Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionLogical.or(this::getExpression, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     * Negate the filter expression.
     *
     * @return This FluentHelper reference.
     */
    @Nonnull
    default FilterableBoolean<EntityT> not()
    {
        final ValueBoolean.Expression expression = FilterExpressionLogical.not(this::getExpression);
        return new Expression<>(expression, getEntityType());
    }
}
