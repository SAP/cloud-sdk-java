package com.sap.cloud.sdk.s4hana.serialization;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The MessageNumber ERP type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@AllArgsConstructor
@EqualsAndHashCode
@Deprecated
public class MessageNumber implements ErpType<MessageNumber>
{
    private static final long serialVersionUID = 3684738474194669793L;

    /**
     * Statically created empty instance of this converter.
     */
    public static final MessageNumber EMPTY = new MessageNumber("");

    @Getter
    @Nonnull
    private final String value;

    @Override
    @Nonnull
    public String toString()
    {
        return value;
    }

    @Nonnull
    @Override
    public ErpTypeConverter<MessageNumber> getTypeConverter()
    {
        return MessageNumberConverter.INSTANCE;
    }

    /**
     * String aggregator for iterable messages.
     *
     * @param messages
     *            The messages to be transformed.
     * @return An accumulated String representation of multiple MessageNumber values.
     */
    @Nonnull
    public static String toString( @Nonnull final Iterable<RemoteFunctionMessage> messages )
    {
        final Function<RemoteFunctionMessage, String> function = new Function<RemoteFunctionMessage, String>()
        {
            @Nullable
            @Override
            public String apply( @Nullable final RemoteFunctionMessage message )
            {
                if( message == null ) {
                    return null;
                }

                return message.getMessageNumber().toString();
            }
        };

        return RemoteFunctionMessage.toString(messages, function);
    }
}
