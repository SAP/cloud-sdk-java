/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * OData filter expression operand for an untyped entity field reference.
 */
public interface FieldUntyped extends FieldReference, FilterableComparisonAbsolute, FilterableComparisonRelative
{
    /**
     * Cast the field reference to a string value, enabling type-safe expressions.
     *
     * @return The string flavored field reference.
     */
    @Nonnull
    default ValueString asString()
    {
        return this::getExpression;
    }

    /**
     * Cast the field reference to a numeric value, enabling type-safe expressions.
     *
     * @return The numeric flavored field reference.
     */
    @Nonnull
    default ValueNumeric asNumber()
    {
        return this::getExpression;
    }

    /**
     * Cast the field reference to a boolean value, enabling type-safe expressions.
     *
     * @return The boolean flavored field reference.
     */
    @Nonnull
    default ValueBoolean asBoolean()
    {
        return this::getExpression;
    }

    /**
     * Cast the field reference to a binary value, enabling type-safe expressions.
     *
     * @return The binary flavored field reference.
     */
    @Nonnull
    default ValueBinary asBinary()
    {
        return this::getExpression;
    }

    /**
     * Cast the field reference to a duration value, enabling type-safe expressions.
     *
     * @return The duration flavored field reference.
     */
    @Nonnull
    default ValueDuration asDuration()
    {
        return this::getExpression;
    }

    /**
     * Cast the field reference to a time-of-day value, enabling type-safe expressions.
     *
     * @return The time-of-day flavored field reference.
     */
    @Nonnull
    default ValueTimeOfDay asTimeOfDay()
    {
        return this::getExpression;
    }

    /**
     * Cast the field reference to an offset-date-time value, enabling type-safe expressions.
     *
     * @return The offset-date-time flavored field reference.
     */
    @Nonnull
    default ValueDateTimeOffset asDateTimeOffset()
    {
        return this::getExpression;
    }

    /**
     * Cast the field reference to a date value, enabling type-safe expressions.
     *
     * @return The date flavored field reference.
     */
    @Nonnull
    default ValueDate asDate()
    {
        return this::getExpression;
    }

    /**
     * Cast the field reference to a collection value, enabling type-safe expressions.
     *
     * @return The collection flavored field reference.
     */
    @Nonnull
    default ValueCollection asCollection()
    {
        return this::getExpression;
    }
}
