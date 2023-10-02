/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import javax.annotation.Nonnull;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * Consumer class for the Listener Pattern to monitor and react on OData actions.
 */
public interface ODataRequestListener
{
    /**
     * Handler to react before execution of an HTTP request.
     *
     * @param request
     *            The HTTP request.
     */
    void listenOnRequest( @Nonnull final HttpRequestBase request );

    /**
     * Handler to react on an error during request generation.
     *
     * @param error
     *            The exception reference.
     */
    void listenOnRequestError( @Nonnull final Exception error );

    /**
     * Handler to react on an error during response parsing.
     *
     * @param error
     *            The exception reference.
     */
    void listenOnParsingError( @Nonnull final Exception error );
}
