package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.UUID;

import javax.annotation.Nonnull;

/**
 * OData filter expression operand of type Edm.Guid
 */
public interface ValueGuid extends Expressions.OperandSingle, FilterableComparisonAbsolute, FilterableComparisonRelative
{
    /**
     * Null value for guid operations.
     */
    @Nonnull
    ValueGuid NULL = Expressions.OperandSingle.NULL::getExpression;

    @Nonnull
    static ValueGuid literal( @Nonnull final UUID v )
    {
        return ( protocol, prefixes ) -> protocol.getUUIDSerializer().apply(v);
    }
}
