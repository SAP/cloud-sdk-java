/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * Adjustable enum for SOAP XML response parsing. Only change the values, when namespaces in response payload differ
 * from ABAP defaults.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@AllArgsConstructor
@Deprecated
public enum SoapNamespace
{
    /**
     * The parent prefix. Default: soap_env
     */
    RESPONSE_PREFIX_SOAP_ENV("soap-env"),

    /**
     * The item prefix. Default: n0
     */
    RESPONSE_PREFIX_N0("n0");

    @Setter( AccessLevel.PUBLIC )
    private String label;

    @Nonnull
    @Override
    public String toString()
    {
        return label;
    }
}
