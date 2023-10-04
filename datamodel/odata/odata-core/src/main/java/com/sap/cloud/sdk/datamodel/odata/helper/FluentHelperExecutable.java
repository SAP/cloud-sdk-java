/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;

/**
 * Representation of any OData V2 request that can be executed.
 *
 * @param <ResultT>
 *            The type of the result entity, if any.
 */
public interface FluentHelperExecutable<ResultT>
{
    /**
     * Executes this request.
     *
     * @param destination
     *            The target system this request should be issued against.
     * @return A response according to the query criteria.
     *
     * @throws DestinationAccessException
     *             If there is an issue accessing the
     *             {@link com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination}.
     * @throws HttpClientInstantiationException
     *             If there is an issue creating the {@link HttpClient}.
     * @throws ODataException
     *             If the OData request execution failed.
     */
    @Nullable
    ResultT executeRequest( @Nonnull final Destination destination );
}
