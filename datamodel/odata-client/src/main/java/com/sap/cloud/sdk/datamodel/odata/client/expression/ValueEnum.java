/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.datamodel.odata.client.expression;

import javax.annotation.Nonnull;

/**
 * OData filter expression operand of enumeration type
 */
public interface ValueEnum extends Expressions.OperandSingle, FilterableComparisonAbsolute
{
    /**
     * Null value for enum operations.
     */
    @Nonnull
    ValueEnum NULL = Expressions.OperandSingle.NULL::getExpression;

    /**
     * Returns a {@link ValueEnum} from the given {@code v}.
     *
     * @param v
     *            The value to be transformed into a {@link ValueEnum}.
     * @return A {@link ValueEnum} that contains the given {@code v}.
     */
    @Nonnull
    static ValueEnum literal( @Nonnull final String v )
    {
        return ( protocol, prefixes ) -> "'" + v + "'";
    }

    /**
     * Returns a {@link ValueEnum} from the given {@code enumType} and {@code v}.
     *
     * @param enumType
     *            The enum type.
     * @param v
     *            The value to be transformed into a {@link ValueEnum}.
     * @return A {@link ValueEnum} that contains the given {@code v}.
     */
    @Nonnull
    static ValueEnum literal( @Nonnull final String enumType, @Nonnull final String v )
    {
        return ( protocol, prefixes ) -> enumType + "'" + v + "'";
    }
}
