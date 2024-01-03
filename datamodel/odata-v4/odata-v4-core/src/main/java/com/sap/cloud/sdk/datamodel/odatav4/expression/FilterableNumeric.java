/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionArithmetic;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueNumeric;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by Number.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 */
public interface FilterableNumeric<EntityT> extends FilterableValue<EntityT, Number>
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
    FilterableNumeric<EntityT> add( @Nonnull final Long operand );

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
    FilterableNumeric<EntityT> add( @Nonnull final Integer operand );

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
    FilterableNumericDecimal<EntityT> add( @Nonnull final Number operand );

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
    FilterableNumeric<EntityT> add( @Nonnull final FilterableNumericInteger<EntityT> operand );

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
    default FilterableNumericDecimal<EntityT> add( @Nonnull final FilterableNumericDecimal<EntityT> operand )
    {
        final ValueNumeric thisNumeric = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final ValueNumeric.Expression expression = FilterExpressionArithmetic.add(thisNumeric, value);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
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
    FilterableNumeric<EntityT> multiply( @Nonnull final Long operand );

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
    FilterableNumeric<EntityT> multiply( @Nonnull final Integer operand );

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
    FilterableNumericDecimal<EntityT> multiply( @Nonnull final Number operand );

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
    FilterableNumeric<EntityT> multiply( @Nonnull final FilterableNumericInteger<EntityT> operand );

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
    default FilterableNumericDecimal<EntityT> multiply( @Nonnull final FilterableNumericDecimal<EntityT> operand )
    {
        final ValueNumeric thisNumeric = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final ValueNumeric.Expression expression = FilterExpressionArithmetic.multiply(thisNumeric, value);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
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
    FilterableNumeric<EntityT> subtract( @Nonnull final Long operand );

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
    FilterableNumeric<EntityT> subtract( @Nonnull final Integer operand );

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
    FilterableNumericDecimal<EntityT> subtract( @Nonnull final Number operand );

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
    FilterableNumeric<EntityT> subtract( @Nonnull final FilterableNumericInteger<EntityT> operand );

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
    default FilterableNumericDecimal<EntityT> subtract( @Nonnull final FilterableNumericDecimal<EntityT> operand )
    {
        final ValueNumeric thisNumeric = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final ValueNumeric.Expression expression = FilterExpressionArithmetic.subtract(thisNumeric, value);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "div".
     *
     * @param operand
     *            The number to divide.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericDecimal<EntityT> divide( @Nonnull final FilterableNumericInteger<EntityT> operand )
    {
        final ValueNumeric thisNumeric = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final ValueNumeric.Expression expression = FilterExpressionArithmetic.divide(thisNumeric, value);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "div".
     *
     * @param operand
     *            The number to divide.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericDecimal<EntityT> divide( @Nonnull final FilterableNumericDecimal<EntityT> operand )
    {
        final ValueNumeric thisNumeric = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final ValueNumeric.Expression expression = FilterExpressionArithmetic.divide(thisNumeric, value);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "div".
     *
     * @param operand
     *            The number to divide.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericDecimal<EntityT> divide( @Nonnull final Number operand )
    {
        final ValueNumeric thisNumeric = this::getExpression;
        final ValueNumeric value = ValueNumeric.literal(operand);
        final ValueNumeric.Expression expression = FilterExpressionArithmetic.divide(thisNumeric, value);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
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
    FilterableNumeric<EntityT> modulo( @Nonnull final Long operand );

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
    FilterableNumeric<EntityT> modulo( @Nonnull final Integer operand );

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
    FilterableNumericDecimal<EntityT> modulo( @Nonnull final Number operand );

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
    FilterableNumeric<EntityT> modulo( @Nonnull final FilterableNumericInteger<EntityT> operand );

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
    default FilterableNumericDecimal<EntityT> modulo( @Nonnull final FilterableNumericDecimal<EntityT> operand )
    {
        final ValueNumeric thisNumeric = this::getExpression;
        final ValueNumeric value = operand::getExpression;
        final ValueNumeric.Expression expression = FilterExpressionArithmetic.modulo(thisNumeric, value);
        return new FilterableNumericDecimal.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "-".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    FilterableNumeric<EntityT> negate();
}
