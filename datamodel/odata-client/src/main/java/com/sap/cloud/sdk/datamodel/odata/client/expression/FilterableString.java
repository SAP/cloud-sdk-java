package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * String operations for generic OData filter expression operands.
 */
public interface FilterableString extends Expressions.Operand
{
    /**
     * Filter by expression "matchesPattern".
     *
     * @param operand
     *            String expression to match the string against.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean matches( @Nonnull final String operand )
    {
        final ValueString value = ValueString.literal(operand);
        return FilterExpressionString.matchesPattern(this::getExpression, value);
    }

    /**
     * Filter by expression "matchesPattern".
     *
     * @param operand
     *            String expression to match the string against.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean matches( @Nonnull final ValueString operand )
    {
        return FilterExpressionString.matchesPattern(this::getExpression, operand);
    }

    /**
     * Filter by expression "tolower".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueString toLower()
    {
        return FilterExpressionString.toLower(this::getExpression);
    }

    /**
     * Filter by expression "toUpper".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueString toUpper()
    {
        return FilterExpressionString.toUpper(this::getExpression);
    }

    /**
     * Filter by expression "trim".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueString trim()
    {
        return FilterExpressionString.trim(this::getExpression);
    }

    /**
     * Filter by expression "length".
     *
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric length()
    {
        return FilterExpressionString.length(this::getExpression);
    }

    /**
     * Filter by expression "concat".
     *
     * @param operand
     *            The string to concatenate with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueString concat( @Nonnull final String operand )
    {
        final ValueString value = ValueString.literal(operand);
        return FilterExpressionString.concat(this::getExpression, value);
    }

    /**
     * Filter by expression "concat".
     *
     * @param operand
     *            The string to concatenate with.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueString concat( @Nonnull final ValueString operand )
    {
        return FilterExpressionString.concat(this::getExpression, operand);
    }

    /**
     * Filter by expression "startswith".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean startsWith( @Nonnull final ValueString operand )
    {
        return FilterExpressionString.startsWith(this::getExpression, operand);
    }

    /**
     * Filter by expression "startswith".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean startsWith( @Nonnull final String operand )
    {
        final ValueString value = ValueString.literal(operand);
        return FilterExpressionString.startsWith(this::getExpression, value);
    }

    /**
     * Filter by expression "endswith".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean endsWith( @Nonnull final ValueString operand )
    {
        return FilterExpressionString.endsWith(this::getExpression, operand);
    }

    /**
     * Filter by expression "endswith".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueBoolean endsWith( @Nonnull final String operand )
    {
        final ValueString value = ValueString.literal(operand);
        return endsWith(value);
    }

    /**
     * Filter by expression "contains".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     * @see #substringOf(ValueString) substringOf(ValueString) for OData V2
     */
    @Nonnull
    default ValueBoolean contains( @Nonnull final ValueString operand )
    {
        return FilterExpressionString.contains(this::getExpression, operand);
    }

    /**
     * Filter by expression "contains".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     * @see #substringOf(String) substringOf(String) for OData V2
     */
    @Nonnull
    default ValueBoolean contains( @Nonnull final String operand )
    {
        final ValueString value = ValueString.literal(operand);
        return contains(value);
    }

    /**
     * Filter by expression "substringof".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     * @see #contains(ValueString) contains(ValueString) for OData V4
     */
    @Nonnull
    default ValueBoolean substringOf( @Nonnull final ValueString operand )
    {
        return FilterExpressionString.substringOf(this::getExpression, operand);
    }

    /**
     * Filter by expression "substringof".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     * @see #contains(String) contains(String) for OData V4
     */
    @Nonnull
    default ValueBoolean substringOf( @Nonnull final String operand )
    {
        final ValueString value = ValueString.literal(operand);
        return substringOf(value);
    }

    /**
     * Filter by expression "indexof".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric indexOf( @Nonnull final String operand )
    {
        final ValueString value = ValueString.literal(operand);
        return indexOf(value);
    }

    /**
     * Filter by expression "indexof".
     *
     * @param operand
     *            The substring which is checked for.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueNumeric indexOf( @Nonnull final ValueString operand )
    {
        return FilterExpressionString.indexOf(this::getExpression, operand);
    }

    /**
     * Filter by expression "substring".
     *
     * @param operand
     *            The number of characters to cut off.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueString substring( @Nonnull final Integer operand )
    {
        final ValueNumeric value = ValueNumeric.literal(operand);
        return FilterExpressionString.substring(this::getExpression, value);
    }

    /**
     * Filter by expression "substring".
     *
     * @param operandIndex
     *            The number of characters to cut off.
     * @param operandLength
     *            The number of characters to keep in.
     * @return The FluentHelper filter.
     */
    @Nonnull
    default ValueString substring( @Nonnull final Integer operandIndex, @Nonnull final Integer operandLength )
    {
        final ValueNumeric value1 = ValueNumeric.literal(operandIndex);
        final ValueNumeric value2 = ValueNumeric.literal(operandLength);
        return FilterExpressionString.substring(this::getExpression, value1, value2);
    }
}
