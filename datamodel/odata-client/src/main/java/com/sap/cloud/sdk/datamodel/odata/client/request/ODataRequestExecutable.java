/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataConnectionException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;

/**
 * General interface for executable OData Requests.
 */
public interface ODataRequestExecutable
{
    /**
     * Execute the OData request with the provided HttpClient reference.
     *
     * @param httpClient
     *            The HttpClient.
     * @return An OData request result.
     * @throws ODataRequestException
     *             When the OData request could not be sent.
     * @throws ODataConnectionException
     *             When the HTTP connection cannot be established.
     * @throws ODataResponseException
     *             When the response code infers an unhealthy state, i.e. when >= 400
     * @throws ODataServiceErrorException
     *             When the response contains an OData error message according to specification.
     */
    @Nonnull
    ODataRequestResult execute( @Nonnull final HttpClient httpClient );
}
