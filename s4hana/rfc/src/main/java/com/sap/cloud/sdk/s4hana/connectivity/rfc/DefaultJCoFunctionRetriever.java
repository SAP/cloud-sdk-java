/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;

class DefaultJCoFunctionRetriever implements JCoFunctionRetriever
{
    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Nonnull
    @Override
    public
        JCoFunction
        retrieveJCoFunction( @Nonnull final String functionName, @Nonnull final JCoDestination destination )
            throws JCoException,
                DestinationAccessException
    {
        final JCoRepository repository = destination.getRepository();
        final JCoFunction function = repository.getFunction(functionName);

        if( function == null ) {
            throw new DestinationAccessException(
                "Could not access function \""
                    + functionName
                    + "\" for "
                    + JCoDestination.class.getSimpleName()
                    + " \""
                    + destination.getDestinationName()
                    + "\". "
                    + "Does this function module \""
                    + functionName
                    + "\" exist in SAP S/4HANA and is it remote-enabled?");
        }

        return function;
    }
}
