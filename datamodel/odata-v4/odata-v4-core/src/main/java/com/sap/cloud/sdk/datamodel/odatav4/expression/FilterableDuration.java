/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import java.time.Duration;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionArithmetic;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionTemporal;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueDuration;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueNumeric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by Duration.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 */
public interface FilterableDuration<EntityT> extends FilterableValue<EntityT, Duration>
{
    /**
     * Wrapper expression class, which delegates to another operation.
     *
     * @param <EntityT>
     *            Type of the entity which references the value.
     */
    @RequiredArgsConstructor
    @Getter
    class Expression<EntityT> implements FilterableDuration<EntityT>, FilterExpression
    {
        @Delegate
        @Nonnull
        private final FilterExpression delegate;

        @Nonnull
        private final Class<EntityT> entityType;
    }

    /**
     *
     * Filter by expression "offsetseconds".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> offsetSeconds()
    {
        final ValueDuration thisDuration = this::getExpression;
        final FilterExpression expression = FilterExpressionTemporal.totalOffsetSeconds(thisDuration);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
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
    default FilterableDuration<EntityT> add( @Nonnull final FilterableDuration<EntityT> operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueDuration value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.add(thisDuration, value);
        return new Expression<>(expression, getEntityType());
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
    default FilterableDuration<EntityT> add( @Nonnull final Duration operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.add(thisDuration, value);
        return new Expression<>(expression, getEntityType());
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
    default FilterableDuration<EntityT> subtract( @Nonnull final FilterableDuration<EntityT> operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueDuration value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisDuration, value);
        return new Expression<>(expression, getEntityType());
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
    default FilterableDuration<EntityT> subtract( @Nonnull final Duration operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueDuration value = ValueDuration.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.subtract(thisDuration, value);
        return new Expression<>(expression, getEntityType());
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
    default FilterableDuration<EntityT> multiply( @Nonnull final FilterableNumeric<EntityT> operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.multiply(thisDuration, value);
        return new Expression<>(expression, getEntityType());
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
    default FilterableDuration<EntityT> multiply( @Nonnull final Number operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueNumeric value = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.multiply(thisDuration, value);
        return new Expression<>(expression, getEntityType());
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
    default FilterableDuration<EntityT> divide( @Nonnull final FilterableNumeric<EntityT> operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.divide(thisDuration, value);
        return new Expression<>(expression, getEntityType());
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
    default FilterableDuration<EntityT> divide( @Nonnull final Number operand )
    {
        final ValueDuration thisDuration = this::getExpression;
        final ValueNumeric value = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionArithmetic.divide(thisDuration, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "-".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableDuration<EntityT> negate()
    {
        final ValueDuration thisDuration = this::getExpression;
        final FilterExpression expression = FilterExpressionArithmetic.negate(thisDuration);
        return new Expression<>(expression, getEntityType());
    }
}
