/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.util.Iterator;
import java.util.function.Function;

import javax.annotation.Nonnull;

import lombok.Value;

/**
 * Wrapper class for remote messages.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Value
@Deprecated
public class RemoteFunctionMessage
{
    MessageType messageType;
    MessageClass messageClass;
    MessageNumber messageNumber;
    String messageText;

    /**
     * Aggregator method to translate iterable messages to String.
     *
     * @param messages
     *            The messages that need to be transformed to String.
     * @param function
     *            The transformation method to be used.
     * @return The aggregates String result.
     */
    @Nonnull
    public static String toString(
        @Nonnull final Iterable<RemoteFunctionMessage> messages,
        @Nonnull final Function<RemoteFunctionMessage, String> function )
    {
        final Iterator<RemoteFunctionMessage> messageIt = messages.iterator();
        final StringBuilder sb = new StringBuilder();

        while( messageIt.hasNext() ) {
            sb.append(function.apply(messageIt.next()));

            if( messageIt.hasNext() ) {
                sb.append(", ");
            }
        }

        return sb + ".";
    }
}
