/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Thrown when the rollback during the invocation of a remote function fails.
 */
public class RemoteFunctionRollbackFailedException extends RemoteFunctionException
{
    private static final long serialVersionUID = -6060469790853917788L;

    /**
     * Default constructor without messages.
     */
    @Deprecated
    public RemoteFunctionRollbackFailedException()
    {
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
    public RemoteFunctionRollbackFailedException( @Nullable final String message )
    {
        super(message);
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
    public RemoteFunctionRollbackFailedException( @Nullable final Throwable cause )
    {
        super(cause);
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
    public RemoteFunctionRollbackFailedException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
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
    public RemoteFunctionRollbackFailedException(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage message )
    {
        super(message);
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
    public RemoteFunctionRollbackFailedException(
        @Nonnull final Iterable<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionMessage> messages )
    {
        super(messages);
    }
}
