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

    @Nonnull
    static ValueEnum literal( @Nonnull final String v )
    {
        return ( protocol, prefixes ) -> "'" + v + "'";
    }

    @Nonnull
    static ValueEnum literal( @Nonnull final String enumType, @Nonnull final String v )
    {
        return ( protocol, prefixes ) -> enumType + "'" + v + "'";
    }
}
