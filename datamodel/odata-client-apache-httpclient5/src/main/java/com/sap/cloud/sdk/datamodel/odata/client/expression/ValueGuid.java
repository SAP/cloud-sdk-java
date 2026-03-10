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

    /**
     * Returns a {@link ValueGuid} from the given {@code v}.
     *
     * @param v
     *            The value to be transformed into a {@link ValueGuid}.
     * @return A {@link ValueGuid} that contains the given {@code v}.
     */
    @Nonnull
    static ValueGuid literal( @Nonnull final UUID v )
    {
        return ( protocol, prefixes ) -> protocol.getUUIDSerializer().apply(v);
    }
}
