/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.exception;

import javax.annotation.Nullable;

import org.apache.http.HttpStatus;

import lombok.Getter;

/**
 * An exception that is thrown when there is an issue reported by the SAP HANA Cloud Connector.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class CloudConnectorException extends RequestExecutionException
{
    private static final long serialVersionUID = 7796577661709521326L;

    @Getter
    private final transient int cloudConnectorStatusCode;

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     */
    public CloudConnectorException( @Nullable final String message )
    {
        this(HttpStatus.SC_INTERNAL_SERVER_ERROR, message);
    }

    /**
     * Constructor.
     *
     * @param cloudConnectorStatusCode
     *            The cloud connector response status code.
     * @param message
     *            The message.
     */
    public CloudConnectorException( final int cloudConnectorStatusCode, @Nullable final String message )
    {
        super(message);
        this.cloudConnectorStatusCode = cloudConnectorStatusCode;
    }

    /**
     * Constructor.
     *
     * @param cause
     *            The error cause.
     */
    public CloudConnectorException( @Nullable final Throwable cause )
    {
        this(HttpStatus.SC_INTERNAL_SERVER_ERROR, cause);
    }

    /**
     * Constructor.
     *
     * @param cloudConnectorStatusCode
     *            The cloud connector response status code.
     * @param cause
     *            The error cause.
     */
    public CloudConnectorException( final int cloudConnectorStatusCode, @Nullable final Throwable cause )
    {
        super(cause);
        this.cloudConnectorStatusCode = cloudConnectorStatusCode;
    }

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     * @param cause
     *            The error cause.
     */
    public CloudConnectorException( @Nullable final String message, @Nullable final Throwable cause )
    {
        this(HttpStatus.SC_INTERNAL_SERVER_ERROR, message, cause);
    }

    /**
     * Constructor.
     *
     * @param cloudConnectorStatusCode
     *            The cloud connector response status code.
     * @param message
     *            The message.
     * @param cause
     *            The error cause.
     */
    public CloudConnectorException(
        final int cloudConnectorStatusCode,
        @Nullable final String message,
        @Nullable final Throwable cause )
    {
        super(message, cause);
        this.cloudConnectorStatusCode = cloudConnectorStatusCode;
    }
}
