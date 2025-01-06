/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;

interface JCoFunctionRetriever
{
    @Nonnull
    JCoFunction retrieveJCoFunction( @Nonnull final String functionName, @Nonnull final JCoDestination destination )
        throws JCoException,
            DestinationAccessException;
}
