package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

/**
 * Set of OData filter functions for collection types.
 */
public interface FilterExpressionCollection
{
    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether the given {@code operand1} has {@code operand2} as
     * a subset ({@code "hassubset"}).
     *
     * @param operand1
     *            The potential super set.
     * @param operand2
     *            The potential subset.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression hasSubset(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("hassubset", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether the given {@code operand1} has {@code operand2} as
     * a subsequence ({@code "hassubsequence"}).
     *
     * @param operand1
     *            The potential super sequence.
     * @param operand2
     *            The potential subsequence.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression hasSubSequence(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("hassubsequence", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueCollection.Expression} that concatenates the given {@code operand1} and {@code operand2}.
     *
     * @param operand1
     *            The first collection-like entity.
     * @param operand2
     *            The second collection-like entity.
     * @return A {@link ValueCollection.Expression}.
     */
    @Nonnull
    static ValueCollection.Expression concat(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("concat", operand1, operand2);
        return new ValueCollection.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether the given {@code operand1} contains
     * {@code operand2} ({@code "contains"}).
     *
     * @param operand1
     *            The potential super set.
     * @param operand2
     *            The potential subset.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression contains(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("contains", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether the given {@code operand1} ends with
     * {@code operand2} ({@code "endswith"}).
     *
     * @param operand1
     *            The potential super set.
     * @param operand2
     *            The potential subset.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression endsWith(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("endswith", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether the given {@code operand1} starts with
     * {@code operand2} ({@code "startswith"}).
     *
     * @param operand1
     *            The potential super set.
     * @param operand2
     *            The potential subset.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression startsWith(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("startswith", operand1, operand2);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that is supposed to return the index of {@code operand1} where
     * {@code operand2} starts ({@code "indexof"}).
     *
     * @param operand1
     *            The potential super set.
     * @param operand2
     *            The potential subset.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression indexOf(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final Expressions.OperandMultiple operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("indexof", operand1, operand2);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueNumeric.Expression} that returns the length of the {@code operand} ({@code "length"}).
     *
     * @param operand
     *            The operand to get the length from.
     * @return A {@link ValueNumeric.Expression}.
     */
    @Nonnull
    static ValueNumeric.Expression length( @Nonnull final Expressions.OperandMultiple operand )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("length", operand);
        return new ValueNumeric.Expression(expression);
    }

    /**
     * Returns a {@link ValueCollection.Expression} that extracts the subsequence of {@code operand1} starting from
     * {@code operand2} ({@code "substring"}).
     *
     * @param operand1
     *            The collection to get the subsequence from.
     *
     * @param operand2
     *            The index of the first element of the subsequence to be extracted.
     * @return A {@link ValueCollection.Expression}.
     */
    @Nonnull
    static
        ValueCollection.Expression
        substring( @Nonnull final Expressions.OperandMultiple operand1, @Nonnull final ValueNumeric operand2 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("substring", operand1, operand2);
        return new ValueCollection.Expression(expression);
    }

    /**
     * Returns a {@link ValueCollection.Expression} that extracts the subsequence of {@code operand1} starting from
     * {@code operand2} with length {@code operand3} ({@code "substring"}).
     *
     * @param operand1
     *            The collection to get the subsequence from.
     * @param operand2
     *            The index of the first element of the subsequence to be extracted.
     * @param operand3
     *            The length of the subsequence to be extracted.
     * @return A {@link ValueCollection.Expression}.
     */
    @Nonnull
    static ValueCollection.Expression substring(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final ValueNumeric operand2,
        @Nonnull final ValueNumeric operand3 )
    {
        final FilterExpression expression = Expressions.createFunctionPrefix("substring", operand1, operand2, operand3);
        return new ValueCollection.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether all elements in {@code operand1} satisfy
     * {@code operand2}.
     *
     * @param operand1
     *            The collection-like entity to be checked.
     * @param operand2
     *            The condition to be satisfied.
     * @param lambdaFieldPredicate
     *            The predicate for which fields will be given a prefix.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression all(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final ValueBoolean operand2,
        @Nonnull final Predicate<FieldReference> lambdaFieldPredicate )
    {
        final FilterExpression expression =
            Expressions.createFunctionLambda("all", operand1, operand2, lambdaFieldPredicate);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether any element in {@code operand1} satisfies
     * {@code operand2}.
     *
     * @param operand1
     *            The collection-like entity to be checked.
     * @param operand2
     *            The condition to be satisfied.
     * @param lambdaFieldPredicate
     *            The predicate for which fields will be given a prefix.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression any(
        @Nonnull final Expressions.OperandMultiple operand1,
        @Nonnull final ValueBoolean operand2,
        @Nonnull final Predicate<FieldReference> lambdaFieldPredicate )
    {
        final FilterExpression expression =
            Expressions.createFunctionLambda("any", operand1, operand2, lambdaFieldPredicate);
        return new ValueBoolean.Expression(expression);
    }

    /**
     * Returns a {@link ValueBoolean.Expression} that checks whether {@code operand1} contains any elements.
     *
     * @param operand1
     *            The collection=like entity to be checked.
     * @return A {@link ValueBoolean.Expression}.
     */
    @Nonnull
    static ValueBoolean.Expression any( @Nonnull final Expressions.OperandMultiple operand1 )
    {
        final FilterExpression expression = Expressions.createFunctionLambda("any", operand1);
        return new ValueBoolean.Expression(expression);
    }
}
