/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.expression;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.expression.Expressions;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpression;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionCollection;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueBoolean;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueCollection;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueNumeric;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Fluent helper class to provide filter functions to OData expressions referenced by Collection.
 *
 * @param <EntityT>
 *            Type of the entity which references the value.
 * @param <ItemT>
 *            Type of the item type the collection holds.
 */
public interface FilterableCollection<EntityT, ItemT> extends Expressions.OperandMultiple, EntityReference<EntityT>
{
    /**
     * Get the item type the collection holds.
     *
     * @return The item type.
     */
    @Nonnull
    Class<ItemT> getItemType();

    /**
     * Wrapper expression class, which delegates to another operation.
     *
     * @param <EntityT>
     *            Type of the entity which references the value.
     * @param <ItemT>
     *            Type of the item type the collection holds.
     */
    @RequiredArgsConstructor
    @Getter
    class Expression<EntityT, ItemT> implements FilterableCollection<EntityT, ItemT>
    {
        @Delegate
        @Nonnull
        private final FilterExpression delegate;

        @Nonnull
        private final Class<EntityT> entityType;

        @Nonnull
        private final Class<ItemT> itemType;
    }

    /**
     * Filter by expression "hasSubset".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> hasSubset( @Nonnull final FilterableCollection<?, ItemT> operand )
    {
        final FilterExpression expression = FilterExpressionCollection.hasSubset(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "hasSubset".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> hasSubset( @Nonnull final Iterable<ItemT> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        final FilterExpression expression = FilterExpressionCollection.hasSubset(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "hasSubSequence".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> hasSubSequence( @Nonnull final FilterableCollection<?, ItemT> operand )
    {
        final FilterExpression expression = FilterExpressionCollection.hasSubSequence(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "hasSubSequence".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> hasSubSequence( @Nonnull final Iterable<ItemT> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        final FilterExpression expression = FilterExpressionCollection.hasSubSequence(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "contains".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> contains( @Nonnull final FilterableCollection<?, ItemT> operand )
    {
        final FilterExpression expression = FilterExpressionCollection.contains(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "contains".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> contains( @Nonnull final Iterable<ItemT> operand )
    {
        final Expressions.OperandMultiple value = ValueCollection.literal(operand);
        final FilterExpression expression = FilterExpressionCollection.contains(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "startsWith".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> startsWith( @Nonnull final FilterableCollection<?, ItemT> operand )
    {
        final FilterExpression expression = FilterExpressionCollection.startsWith(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "startsWith".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> startsWith( @Nonnull final Iterable<ItemT> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        final FilterExpression expression = FilterExpressionCollection.startsWith(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "endsWith".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> endsWith( @Nonnull final FilterableCollection<?, ItemT> operand )
    {
        final FilterExpression expression = FilterExpressionCollection.endsWith(this, operand);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "endsWith".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> endsWith( @Nonnull final Iterable<ItemT> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        final FilterExpression expression = FilterExpressionCollection.endsWith(this, value);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "indexOf".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> indexOf( @Nonnull final FilterableCollection<?, ItemT> operand )
    {
        final FilterExpression expression = FilterExpressionCollection.indexOf(this, operand);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "indexOf".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> indexOf( @Nonnull final Iterable<ItemT> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        final FilterExpression expression = FilterExpressionCollection.indexOf(this, value);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "concat".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableCollection<EntityT, ItemT> concat( @Nonnull final FilterableCollection<?, ItemT> operand )
    {
        final FilterExpression expression = FilterExpressionCollection.concat(this, operand);
        return new Expression<>(expression, getEntityType(), getItemType());
    }

    /**
     * Filter by expression "concat".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableCollection<EntityT, ItemT> concat( @Nonnull final Iterable<ItemT> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        final FilterExpression expression = FilterExpressionCollection.concat(this, value);
        return new Expression<>(expression, getEntityType(), getItemType());
    }

    /**
     * Filter by expression "length".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableNumericInteger<EntityT> length()
    {
        final FilterExpression expression = FilterExpressionCollection.length(this);
        return new FilterableNumericInteger.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by expression "substring".
     *
     * @param operand
     *            Only operand of Integer type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableCollection<EntityT, ItemT> substring( @Nonnull final Integer operand )
    {
        final ValueNumeric value = ValueNumeric.literal(operand);
        final FilterExpression expression = FilterExpressionCollection.substring(this, value);
        return new Expression<>(expression, getEntityType(), getItemType());
    }

    /**
     * Filter by expression "substring".
     *
     * @param operandIndex
     *            Operand of Integer type to mark the start of the subset.
     * @param operandLength
     *            Operand of Integer type to mark the size of the subset.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default
        FilterableCollection<EntityT, ItemT>
        substring( @Nonnull final Integer operandIndex, @Nonnull final Integer operandLength )
    {
        final ValueNumeric value1 = ValueNumeric.literal(operandIndex);
        final ValueNumeric value2 = ValueNumeric.literal(operandLength);
        final ValueCollection.Expression expression = FilterExpressionCollection.substring(this, value1, value2);
        return new Expression<>(expression, getEntityType(), getItemType());
    }

    /**
     * Filter by lambda expression "all".
     *
     * @param operand
     *            Operand to provide a generic filter to the collection item.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> all( @Nonnull final FilterableBoolean<ItemT> operand )
    {
        final Predicate<FieldReference> lambdaFieldPredicate =
            o -> o instanceof EntityReference && ((EntityReference<?>) o).getEntityType().equals(getItemType());
        final ValueBoolean.Expression expression =
            FilterExpressionCollection.all(this, operand::getExpression, lambdaFieldPredicate);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }

    /**
     * Filter by lambda expression "any".
     *
     * @param operand
     *            Operand to provide a generic filter to the collection item.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default FilterableBoolean<EntityT> any( @Nonnull final FilterableBoolean<ItemT> operand )
    {
        final Predicate<FieldReference> lambdaFieldPredicate =
            o -> o instanceof EntityReference && ((EntityReference<?>) o).getEntityType().equals(getItemType());
        final FilterExpression expression =
            FilterExpressionCollection.any(this, operand::getExpression, lambdaFieldPredicate);
        return new FilterableBoolean.Expression<>(expression, getEntityType());
    }
}
