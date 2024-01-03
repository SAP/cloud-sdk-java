/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.Expressions;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionString;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueNumeric;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueString;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by String.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 */
public interface FilterableString<EntityT> extends FilterableValue<EntityT, String>
{
    /**
     * Wrapper expression class, which delegates to another operation.
     *
     * @param <EntityT>
     *            Type of the entity which references the value.
     */
    @RequiredArgsConstructor
    @Getter
    class Expression<EntityT> implements FilterableString<EntityT>, FilterExpression
    {
        @Delegate
        @Nonnull
        private final FilterExpression delegate;

        @Nonnull
        private final Class<EntityT> entityType;
    }

    /**
     *
     * Filter by expression "matchesPattern".
     *
     * @param operand
     *            String expression to match the string against.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> matches( @Nonnull final String operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueString value = (ValueString) Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionString.matchesPattern(thisString, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "matchesPattern".
     *
     * @param operand
     *            String expression to match the string against.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> matches( @Nonnull final FilterableString<EntityT> operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueString value = operand::getExpression;
        final ValueBoolean.Expression expression = FilterExpressionString.matchesPattern(thisString, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "tolower".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableString<EntityT> toLower()
    {
        final ValueString.Expression expression = FilterExpressionString.toLower(this::getExpression);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "toupper".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableString<EntityT> toUpper()
    {
        final ValueString.Expression expression = FilterExpressionString.toUpper(this::getExpression);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "trim".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableString<EntityT> trim()
    {
        final ValueString.Expression expression = FilterExpressionString.trim(this::getExpression);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "length".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> length()
    {
        final ValueNumeric.Expression expression = FilterExpressionString.length(this::getExpression);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "concat".
     *
     * @param operand
     *            The string to concatenate with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableString<EntityT> concat( @Nonnull final FilterableString<EntityT> operand )
    {
        final ValueString value = operand::getExpression;
        final ValueString.Expression expression = FilterExpressionString.concat(this::getExpression, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "concat".
     *
     * @param operand
     *            The string to concatenate with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableString<EntityT> concat( @Nonnull final String operand )
    {
        final ValueString value = (ValueString) Expressions.createOperand(operand);
        final ValueString.Expression expression = FilterExpressionString.concat(this::getExpression, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "startswith".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> startsWith( @Nonnull final FilterableString<EntityT> operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueString value = operand::getExpression;
        final ValueBoolean.Expression expression = FilterExpressionString.startsWith(thisString, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "startswith".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> startsWith( @Nonnull final String operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueString value = (ValueString) Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionString.startsWith(thisString, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "endswith".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> endsWith( @Nonnull final FilterableString<EntityT> operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueString value = operand::getExpression;
        final ValueBoolean.Expression expression = FilterExpressionString.endsWith(thisString, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "endswith".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> endsWith( @Nonnull final String operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueString value = (ValueString) Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionString.endsWith(thisString, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "contain".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> contains( @Nonnull final FilterableString<EntityT> operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueString value = operand::getExpression;
        final ValueBoolean.Expression expression = FilterExpressionString.contains(thisString, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "contain".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> contains( @Nonnull final String operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueString value = (ValueString) Expressions.createOperand(operand);
        final ValueBoolean.Expression expression = FilterExpressionString.contains(thisString, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "indexof".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> indexOf( @Nonnull final FilterableString<EntityT> operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueString value = operand::getExpression;
        final ValueNumeric.Expression expression = FilterExpressionString.indexOf(thisString, value);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "indexof".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> indexOf( @Nonnull final String operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueString value = (ValueString) Expressions.createOperand(operand);
        final ValueNumeric.Expression expression = FilterExpressionString.indexOf(thisString, value);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "substring".
     *
     * @param operand
     *            The number of characters to cut off.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableString<EntityT> substring( @Nonnull final Integer operand )
    {
        final ValueString thisString = this::getExpression;
        final ValueNumeric value = (ValueNumeric) Expressions.createOperand(operand);
        final ValueString.Expression expression = FilterExpressionString.substring(thisString, value);
        return new Expression<>(expression, getEntityType());
    }

    /**
     *
     * Filter by expression "substring".
     *
     * @param operandIndex
     *            The number of characters to cut off.
     * @param operandLength
     *            The number of characters to keep in.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default
        FilterableString<EntityT>
        substring( @Nonnull final Integer operandIndex, @Nonnull final Integer operandLength )
    {
        final ValueString thisString = this::getExpression;
        final ValueNumeric index = (ValueNumeric) Expressions.createOperand(operandIndex);
        final ValueNumeric length = (ValueNumeric) Expressions.createOperand(operandLength);
        final ValueString.Expression expression = FilterExpressionString.substring(thisString, index, length);
        return new Expression<>(expression, getEntityType());
    }
}
