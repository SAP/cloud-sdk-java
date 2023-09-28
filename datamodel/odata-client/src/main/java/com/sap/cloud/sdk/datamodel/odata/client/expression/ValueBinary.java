package com.sap.cloud.sdk.datamodel.odata.client.expression;

import java.util.Base64;
import java.util.function.Function;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * OData filter expression operand of type Edm.String
 */
public interface ValueBinary extends Expressions.OperandSingle, FilterableComparisonAbsolute
{
    /**
     * Null value for binary operations.
     */
    @Nonnull
    ValueBinary NULL = Expressions.OperandSingle.NULL::getExpression;

    /**
     * Lambda to translate a byte array to String.
     */
    Function<byte[], String> ENCODE_TO_STRING = Base64.getEncoder()::encodeToString;

    /**
     * Lambda to translate a String with "ISO-8859-1" encoding to byte array.
     */
    Function<String, byte[]> DECODE_FROM_STRING = Base64.getDecoder()::decode;

    @Nonnull
    static ValueBinary literal( @Nonnull final byte[] v )
    {
        final String value = ENCODE_TO_STRING.apply(v);
        return ( protocol, prefixes ) -> "binary'" + value + "'";
    }

    @RequiredArgsConstructor
    class Expression implements FilterExpression, ValueBinary
    {
        @Delegate
        private final FilterExpression delegate;
    }
}
