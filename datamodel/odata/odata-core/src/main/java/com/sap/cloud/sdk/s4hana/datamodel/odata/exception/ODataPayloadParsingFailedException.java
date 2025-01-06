/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.exception;

import javax.annotation.Nullable;

/**
 * ODataPayloadParsingFailedException will be thrown whenever the VDM encounters errors during parsing that cannot be
 * recovered from. Possible causes are when the ODataJsonMapResolver is instructed to throw exceptions when encountering
 * null values or when TypeConverters for specific fields cannot be instantiated.
 */
public class ODataPayloadParsingFailedException extends RuntimeException
{
    private static final long serialVersionUID = 6446357797006978261L;

    /**
     * Returns a new ODataPayloadParsingFailedException instance.
     *
     * @param message
     *            The error message.
     * @param cause
     *            The exception causing the error.
     */
    public ODataPayloadParsingFailedException( @Nullable final String message, @Nullable final Exception cause )
    {
        super(message, cause);
    }

    /**
     * Returns a new ODataPayloadParsingFailedException instance.
     *
     * @param message
     *            The error message.
     */
    public ODataPayloadParsingFailedException( @Nullable final String message )
    {
        super(message);
    }
}
