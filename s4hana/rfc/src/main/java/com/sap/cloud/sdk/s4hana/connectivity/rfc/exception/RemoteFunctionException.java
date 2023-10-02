/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc.exception;

import java.util.Collections;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;

/**
 * Thrown when something goes wrong during the invocation of a remote function.
 */
@SuppressWarnings( "deprecation" )
public class RemoteFunctionException extends com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
{
    private static final long serialVersionUID = 724035446482779305L;

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Getter
    @Deprecated
    private final transient Iterable<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> messages;

    @Deprecated
    private static class RemoteFunctionMessageStringFunction
        implements
        Function<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage, String>
    {
        /**
         * @deprecated This module will be discontinued, along with its classes and methods.
         */
        @Nullable
        @Override
        @Deprecated
        public String apply( @Nullable final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message )
        {
            if( message == null ) {
                return null;
            }

            return message.getMessageText();
        }
    }

    /**
     * Default constructor without messages.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public RemoteFunctionException()
    {
        messages = Collections.emptyList();
    }

    /**
     * Constructor.
     *
     * @param message
     *            The exception message.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public RemoteFunctionException( @Nullable final String message )
    {
        super(message);
        messages = Collections.emptyList();
    }

    /**
     * Constructor.
     *
     * @param cause
     *            The exception cause.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public RemoteFunctionException( @Nullable final Throwable cause )
    {
        super(cause);
        messages = Collections.emptyList();
    }

    /**
     * Constructor.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception cause.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public RemoteFunctionException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
        messages = Collections.emptyList();
    }

    /**
     * Constructor.
     *
     * @param message
     *            The remote function message.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public RemoteFunctionException(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message )
    {
        this(Collections.singletonList(message));
    }

    /**
     * Constructor.
     *
     * @param messages
     *            The remote function messages.
     *
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    public RemoteFunctionException(
        @Nonnull final Iterable<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> messages )
    {
        super(
            com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage
                .toString(messages, new RemoteFunctionMessageStringFunction()));
        this.messages = messages;
    }
}
