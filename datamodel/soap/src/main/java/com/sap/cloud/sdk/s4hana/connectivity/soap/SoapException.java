/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.soap;

import javax.annotation.Nonnull;

import lombok.NoArgsConstructor;

/**
 * Thrown in case of error situations related to the invocation of SOAP services in an ERP system.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@NoArgsConstructor
@Deprecated
public class SoapException extends Exception
{
    private static final long serialVersionUID = 8062056192440637260L;

    /**
     * Creates an instance of SoapException which indicates an error situation related to invocation of an SOAP Service
     * in SAP S/4HANA.
     *
     * @param cause
     *            Throwable causing the SoapException
     */
    public SoapException( @Nonnull final Throwable cause )
    {
        super(cause);
    }

    /**
     * Creates an instance of SoapException which indicates an error situation related to invocation of an SOAP Service
     * in SAP S/4HANA.
     *
     * @param message
     *            Message describing the cause of this exception
     */
    public SoapException( @Nonnull final String message )
    {
        super(message);
    }

    /**
     * Creates an instance of SoapException which indicates an error situation related to invocation of an SOAP Service
     * in SAP S/4HANA.
     *
     * @param message
     *            Message describing the cause of this exception
     * @param cause
     *            Throwable causing the SoapException
     */
    public SoapException( @Nonnull final String message, @Nonnull final Throwable cause )
    {
        super(message, cause);
    }
}
