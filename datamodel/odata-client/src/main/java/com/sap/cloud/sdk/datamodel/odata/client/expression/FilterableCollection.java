/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * Collection operations for generic OData filter expression operands.
 */
public interface FilterableCollection extends Expressions.OperandMultiple
{
    /**
     * Filter by expression "hasSubset".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression hasSubset( @Nonnull final ValueCollection operand )
    {
        return FilterExpressionCollection.hasSubset(this, operand);
    }

    /**
     * Filter by expression "hasSubset".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression hasSubset( @Nonnull final Iterable<?> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        return FilterExpressionCollection.hasSubset(this, value);
    }

    /**
     * Filter by expression "hasSubSequence".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression hasSubSequence( @Nonnull final ValueCollection operand )
    {
        return FilterExpressionCollection.hasSubSequence(this, operand);
    }

    /**
     * Filter by expression "hasSubSequence".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression hasSubSequence( @Nonnull final Iterable<?> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        return FilterExpressionCollection.hasSubSequence(this, value);
    }

    /**
     * Filter by expression "contains".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression contains( @Nonnull final ValueCollection operand )
    {
        return FilterExpressionCollection.contains(this, operand);
    }

    /**
     * Filter by expression "contains".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression contains( @Nonnull final Iterable<?> operand )
    {
        final Expressions.OperandMultiple value = ValueCollection.literal(operand);
        return FilterExpressionCollection.contains(this, value);
    }

    /**
     * Filter by expression "startsWith".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression startsWith( @Nonnull final ValueCollection operand )
    {
        return FilterExpressionCollection.startsWith(this, operand);
    }

    /**
     * Filter by expression "startsWith".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression startsWith( @Nonnull final Iterable<?> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        return FilterExpressionCollection.startsWith(this, value);
    }

    /**
     * Filter by expression "endsWith".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression endsWith( @Nonnull final ValueCollection operand )
    {
        return FilterExpressionCollection.endsWith(this, operand);
    }

    /**
     * Filter by expression "endsWith".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression endsWith( @Nonnull final Iterable<?> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        return FilterExpressionCollection.endsWith(this, value);
    }

    /**
     * Filter by expression "indexOf".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression indexOf( @Nonnull final ValueCollection operand )
    {
        return FilterExpressionCollection.indexOf(this, operand);
    }

    /**
     * Filter by expression "indexOf".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression indexOf( @Nonnull final Iterable<?> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        return FilterExpressionCollection.indexOf(this, value);
    }

    /**
     * Filter by expression "concat".
     *
     * @param operand
     *            Only operand of collection type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueCollection.Expression concat( @Nonnull final ValueCollection operand )
    {
        return FilterExpressionCollection.concat(this, operand);
    }

    /**
     * Filter by expression "concat".
     *
     * @param operand
     *            Only operand of Java iterable.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueCollection.Expression concat( @Nonnull final Iterable<?> operand )
    {
        final ValueCollection value = ValueCollection.literal(operand);
        return FilterExpressionCollection.concat(this, value);
    }

    /**
     * Filter by expression "length".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric.Expression length()
    {
        return FilterExpressionCollection.length(this);
    }

    /**
     * Filter by expression "substring".
     *
     * @param operand
     *            Only operand of Integer type.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueCollection.Expression substring( @Nonnull final Integer operand )
    {
        final ValueNumeric value = ValueNumeric.literal(operand);
        return FilterExpressionCollection.substring(this, value);
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
        ValueCollection.Expression
        substring( @Nonnull final Integer operandIndex, @Nonnull final Integer operandLength )
    {
        final ValueNumeric value1 = ValueNumeric.literal(operandIndex);
        final ValueNumeric value2 = ValueNumeric.literal(operandLength);
        return FilterExpressionCollection.substring(this, value1, value2);
    }

    /**
     * Filter by lambda expression "all".
     *
     * @param operand
     *            Operand to provide a generic filter to the collection item.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression all( @Nonnull final ValueBoolean operand )
    {
        return FilterExpressionCollection.all(this, operand, o -> true);
    }

    /**
     * Filter by lambda expression "any".
     *
     * @param operand
     *            Operand to provide a generic filter to the collection item.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression any( @Nonnull final ValueBoolean operand )
    {
        return FilterExpressionCollection.any(this, operand, o -> true);
    }

    /**
     * Filter by lambda expression "any", for finding non-empty collections.
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean.Expression any()
    {
        return FilterExpressionCollection.any(this);
    }
}
